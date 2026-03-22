package com.ciblorgasport.notificationsservice.analytics;

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

    /**
     * Trackage d'un envoi de notifications déclenché par Kafka.
     * Appelé après commit de la transaction dans NotificationGeneratorService.
     *
     * @param notificationType type métier : "INCIDENT", "EPREUVE_RAPPEL", etc.
     * @param recipientCount   nombre de destinataires effectivement notifiés
     */
    @Async
    public void trackNotificationSent(String notificationType, int recipientCount) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("eventType", "NOTIFICATION_SENT");
            body.put("endpoint", "kafka/notification");
            body.put("httpMethod", "KAFKA");
            body.put("statusCode", 200);
            body.put("durationMs", 0);
            body.put("userRole", "SYSTEM");
            body.put("timestamp", LocalDateTime.now().toString());
            body.put("metadata", "{\"type\": \"" + notificationType + "\", \"recipients\": " + recipientCount + "}");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(analyticsServiceUrl + "/api/analytics/events/track", entity, Void.class);
        } catch (Exception e) {
            log.warn("[Analytics] Echec envoi evenement NOTIFICATION_SENT : {}", e.getMessage());
        }
    }
}
