package com.ciblorgasport.analyticsservice.aspect;

import com.ciblorgasport.analyticsservice.dto.EventLogRequest;
import com.ciblorgasport.analyticsservice.service.EventLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * Intercepte les requêtes passant par les @RestController du analytics-service.
 * Les requêtes des autres services arrivent via POST /api/analytics/events/track (Gateway Filter).
 */
@Aspect
@Component
public class AnalyticsAspect {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsAspect.class);

    private final EventLogService eventLogService;

    public AnalyticsAspect(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object trackRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        HttpServletRequest httpRequest = getCurrentRequest();

        // Exclure les endpoints analytics eux-mêmes, actuator, et OPTIONS
        if (httpRequest != null) {
            String uri = httpRequest.getRequestURI();
            String method = httpRequest.getMethod();
            if (uri.startsWith("/api/analytics") || uri.startsWith("/actuator") || "OPTIONS".equalsIgnoreCase(method)) {
                return joinPoint.proceed();
            }
        }

        Object result = null;
        int statusCode = 200;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            statusCode = 500;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            try {
                logEvent(httpRequest, statusCode, duration);
            } catch (Exception e) {
                log.warn("Échec du logging AOP analytics : {}", e.getMessage());
            }
        }
        return result;
    }

    private void logEvent(HttpServletRequest request, int statusCode, long durationMs) {
        if (request == null) return;

        EventLogRequest event = new EventLogRequest();
        event.setTimestamp(LocalDateTime.now());
        event.setEndpoint(request.getRequestURI());
        event.setHttpMethod(request.getMethod());
        event.setStatusCode(statusCode);
        event.setDurationMs(durationMs);
        event.setIpAddress(extractIp(request));
        event.setEventType(resolveEventType(request.getRequestURI(), request.getMethod()));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            event.setUserRole(auth.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .orElse("ANONYMOUS"));
        } else {
            event.setUserRole("ANONYMOUS");
        }

        eventLogService.saveAsync(event);
    }

    private String resolveEventType(String uri, String method) {
        if (uri == null) return "PAGE_VIEW";
        if (uri.contains("/auth/login")) return "USER_LOGIN";
        if (uri.contains("/auth/logout")) return "USER_LOGOUT";
        if (uri.contains("/auth/register")) return "USER_REGISTER";
        if (uri.contains("/epreuves")) return "EPREUVE_VIEW";
        if (uri.contains("/competitions")) return "COMPETITION_VIEW";
        if (uri.contains("/events")) return "EVENT_VIEW";
        if (uri.contains("/equipe")) return "EQUIPE_VIEW";
        if (uri.contains("/athlete") || uri.contains("/commissaire")) {
            return "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)
                    ? "ATHLETE_VALIDATION" : "ATHLETE_PROFILE_VIEW";
        }
        if (uri.contains("/results") || uri.contains("/resultats")) {
            if ("POST".equalsIgnoreCase(method)) return "RESULT_SUBMIT";
            if ("PUT".equalsIgnoreCase(method) && uri.contains("/publish")) return "RESULT_PUBLISHED";
            return "RESULT_VIEW";
        }
        if (uri.contains("/incidents")) {
            return "POST".equalsIgnoreCase(method) ? "INCIDENT_DECLARED" : "INCIDENT_VIEW";
        }
        return "PAGE_VIEW";
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
