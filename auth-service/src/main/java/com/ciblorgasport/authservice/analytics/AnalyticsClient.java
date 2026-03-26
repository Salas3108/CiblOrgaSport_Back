package com.ciblorgasport.authservice.analytics;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class AnalyticsClient {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsClient.class);

    private final RestTemplate restTemplate;

    @Value("${analytics.service.url:http://localhost:8092}")
    private String analyticsServiceUrl;

    public AnalyticsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public void track(HttpServletRequest request, int statusCode, long durationMs,
                      Long userId, String userRole) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("userId", userId);
            body.put("userRole", userRole != null ? userRole : "ANONYMOUS");
            body.put("endpoint", request.getRequestURI());
            body.put("httpMethod", request.getMethod());
            body.put("statusCode", statusCode);
            body.put("durationMs", durationMs);
            body.put("ipAddress", extractIp(request));
            body.put("eventType", resolveEventType(request.getRequestURI(), request.getMethod()));
            body.put("timestamp", LocalDateTime.now().toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(analyticsServiceUrl + "/api/analytics/events/track", entity, Void.class);
        } catch (Exception e) {
            log.warn("[Analytics] Échec envoi événement (service down?) : {}", e.getMessage());
        }
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) return forwarded.split(",")[0].trim();
        return request.getRemoteAddr();
    }

    private String resolveEventType(String uri, String method) {
        if (uri == null) return "PAGE_VIEW";
        if (uri.contains("/auth/login")) return "USER_LOGIN";
        if (uri.contains("/auth/logout")) return "USER_LOGOUT";
        if (uri.contains("/auth/register")) return "USER_REGISTER";
        if (uri.contains("/admin/validate-volunteer")) return "VOLUNTEER_VALIDATED";
        return "PAGE_VIEW";
    }
}
