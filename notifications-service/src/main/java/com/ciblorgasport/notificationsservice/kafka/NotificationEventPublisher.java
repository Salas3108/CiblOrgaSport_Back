package com.ciblorgasport.notificationsservice.kafka;

import com.ciblorgasport.notificationsservice.kafka.dto.NotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String notificationsTopic;

    public NotificationEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                     ObjectMapper objectMapper,
                                     @Value("${ciblorgasport.kafka.topics.notifications}") String notificationsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.notificationsTopic = notificationsTopic;
    }

    public void publish(NotificationEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            String key = event.getTargetType() + ":" + (event.getTargetUserId() != null ? event.getTargetUserId() : "")
                    + ":" + (event.getTargetCompetitionId() != null ? event.getTargetCompetitionId() : "");
            kafkaTemplate.send(notificationsTopic, key, payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize NotificationEvent", e);
        }
    }
}
