package com.ciblorgasport.eventservice.analytics;

import com.ciblorgasport.eventservice.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AnalyticsAspect {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsAspect.class);

    private final AnalyticsClient analyticsClient;
    private final JwtUtils jwtUtils;

    public AnalyticsAspect(AnalyticsClient analyticsClient, JwtUtils jwtUtils) {
        this.analyticsClient = analyticsClient;
        this.jwtUtils = jwtUtils;
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object trackRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        HttpServletRequest request = getCurrentRequest();

        if (request != null) {
            String uri = request.getRequestURI();
            String method = request.getMethod();
            if (uri.startsWith("/api/analytics") || uri.startsWith("/actuator")
                    || "OPTIONS".equalsIgnoreCase(method)) {
                return joinPoint.proceed();
            }
        }

        Object result = null;
        int statusCode = 200;
        try {
            result = joinPoint.proceed();
            if (result instanceof ResponseEntity) {
                statusCode = ((ResponseEntity<?>) result).getStatusCode().value();
            }
        } catch (Exception e) {
            statusCode = 500;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            try {
                Long userId = extractUserId(request);
                String userRole = extractUserRole();
                analyticsClient.track(request, statusCode, duration, userId, userRole);
            } catch (Exception e) {
                log.warn("[Analytics] Erreur AOP : {}", e.getMessage());
            }
        }
        return result;
    }

    private Long extractUserId(HttpServletRequest request) {
        if (request == null) return null;
        try {
            String header = request.getHeader("Authorization");
            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (jwtUtils.validateJwtToken(token)) {
                    return jwtUtils.getUserIdFromJwtToken(token);
                }
            }
        } catch (Exception e) {
            log.debug("[Analytics] userId non extrait : {}", e.getMessage());
        }
        return null;
    }

    private String extractUserRole() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                return auth.getAuthorities().stream()
                        .findFirst()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .orElse("ANONYMOUS");
            }
        } catch (Exception ignored) {}
        return "ANONYMOUS";
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
