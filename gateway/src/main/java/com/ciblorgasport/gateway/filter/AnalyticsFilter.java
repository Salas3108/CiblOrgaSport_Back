package com.ciblorgasport.gateway.filter;

import com.ciblorgasport.gateway.security.JwtUtils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalFilter du gateway qui intercepte toutes les requêtes et envoie
 * les données analytics à l'analytics-service de façon asynchrone (fire-and-forget).
 * Ne bloque jamais la requête principale.
 */
@Component
public class AnalyticsFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsFilter.class);
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final WebClient webClient;
    private final JwtUtils jwtUtils;

    @Value("${analytics.service.url:http://localhost:8090}")
    private String analyticsServiceUrl;

    public AnalyticsFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.webClient = WebClient.builder()
                .codecs(config -> config.defaultCodecs().maxInMemorySize(256 * 1024))
                .build();
    }

    @Override
    public int getOrder() {
        // Exécuté après le filtre d'authentification (order = -1)
        return 0;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod() != null ? request.getMethod().name() : "";

        // Exclusions : analytics lui-même, actuator, OPTIONS
        if (path.startsWith("/api/analytics") || path.startsWith("/actuator") || "OPTIONS".equalsIgnoreCase(method)) {
            return chain.filter(exchange);
        }

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).doFinally(signal -> {
            try {
                long duration = System.currentTimeMillis() - startTime;
                ServerHttpResponse response = exchange.getResponse();
                var sc = response.getStatusCode();
                int statusCode = sc != null ? sc.value() : 0;

                Map<String, Object> payload = buildPayload(request, statusCode, duration);
                sendToAnalytics(payload);
            } catch (Exception e) {
                log.warn("[AnalyticsFilter] Erreur lors de la préparation du payload : {}", e.getMessage());
            }
        });
    }

    private Map<String, Object> buildPayload(ServerHttpRequest request, int statusCode, long durationMs) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("endpoint", request.getURI().getPath());
        payload.put("httpMethod", request.getMethod().name());
        payload.put("statusCode", statusCode);
        payload.put("durationMs", durationMs);
        payload.put("timestamp", LocalDateTime.now().format(ISO_FORMAT));
        payload.put("ipAddress", extractIp(request));
        String httpMethod = request.getMethod() != null ? request.getMethod().name() : "";
        payload.put("eventType", resolveEventType(request.getURI().getPath(), httpMethod));

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtils.validateJwtToken(token)) {
                    Claims claims = jwtUtils.getClaimsFromJwtToken(token);
                    String role = claims.get("role", String.class);
                    if (role != null) {
                        payload.put("userRole", role.replace("ROLE_", ""));
                    }
                    Object userId = claims.get("userId");
                    if (userId != null) {
                        payload.put("userId", userId);
                    }
                }
            } catch (Exception e) {
                // JWT invalide ou expiré — on log en ANONYMOUS
            }
        }

        if (!payload.containsKey("userRole")) {
            payload.put("userRole", "ANONYMOUS");
        }

        return payload;
    }

    private void sendToAnalytics(Map<String, Object> payload) {
        webClient.post()
                .uri(analyticsServiceUrl + "/api/analytics/events/track")
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofMillis(500))
                .subscribe(
                        response -> { /* fire-and-forget, succès silencieux */ },
                        error -> log.debug("[AnalyticsFilter] analytics-service injoignable : {}", error.getMessage())
                );
    }

    private String resolveEventType(String path, String method) {
        if (path == null) return "PAGE_VIEW";
        if (path.contains("/auth/login")) return "USER_LOGIN";
        if (path.contains("/auth/logout")) return "USER_LOGOUT";
        if (path.contains("/auth/register")) return "USER_REGISTER";
        if (path.contains("/admin/validate-volunteer")) return "VOLUNTEER_VALIDATED";
        if (path.contains("/epreuves")) return "EPREUVE_VIEW";
        if (path.contains("/competitions")) return "COMPETITION_VIEW";
        if (path.contains("/events")) return "EVENT_VIEW";
        if (path.contains("/results") || path.contains("/resultats")) return "RESULT_VIEW";
        if (path.contains("/athlete")) return "ATHLETE_PROFILE_VIEW";
        if (path.contains("/notifications") && "POST".equalsIgnoreCase(method)) return "NOTIFICATION_SENT";
        if (path.contains("/abonnements") || path.contains("/subscribe")) return "NOTIFICATION_SUBSCRIBED";
        if (path.contains("/incidents")) return "INCIDENT_DECLARED";
        return "PAGE_VIEW";
    }

    private String extractIp(ServerHttpRequest request) {
        String forwarded = request.getHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }
        return "unknown";
    }
}
