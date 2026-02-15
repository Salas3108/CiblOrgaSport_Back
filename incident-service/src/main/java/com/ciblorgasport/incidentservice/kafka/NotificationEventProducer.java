package com.ciblorgasport.incidentservice.kafka;

import com.ciblorgasport.incidentservice.kafka.dto.IncidentEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String incidentsTopic;

    public NotificationEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                     ObjectMapper objectMapper,
                                     @Value("${ciblorgasport.kafka.topics.incidents}") String incidentsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.incidentsTopic = incidentsTopic;
    }

    public void publishIncidentCreated(IncidentEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(incidentsTopic, String.valueOf(event.getIncidentId()), payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize IncidentEvent", e);
        }
    }
}
