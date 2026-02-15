package com.ciblorgasport.incidentservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class IncidentEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String incidentsTopic;

    public IncidentEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                 ObjectMapper objectMapper,
                                 @Value("${ciblorgasport.kafka.topics.incidents:incidents-topic}") String incidentsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.incidentsTopic = incidentsTopic;
    }

    public void publishIncident(com.ciblorgasport.incidentservice.model.Incident incident) {
        try {
            IncidentEvent event = new IncidentEvent();
            event.setIncidentId(incident.getId());
            event.setType(incident.getType() != null ? incident.getType().name() : "UNKNOWN");
            event.setLocation(incident.getLocation());
            event.setImpactLevel(incident.getImpactLevel() != null ? incident.getImpactLevel().name() : null);
            event.setDescription(incident.getDescription());
            event.setCreatedAt(incident.getReportedAt() != null ? incident.getReportedAt().toInstant(java.time.ZoneOffset.UTC) : java.time.Instant.now());

            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(incidentsTopic, String.valueOf(event.getIncidentId()), payload);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot publish incident event", e);
        }
    }
}
