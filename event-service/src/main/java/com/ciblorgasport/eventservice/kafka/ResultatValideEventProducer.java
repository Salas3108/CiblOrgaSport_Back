package com.ciblorgasport.eventservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ResultatValideEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String resultatsTopic;

    public ResultatValideEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                       ObjectMapper objectMapper,
                                       @Value("${ciblorgasport.kafka.topics.resultats:resultats-topic}") String resultatsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.resultatsTopic = resultatsTopic;
    }

    public void publishResultat(UUID competitionId, Long epreuveId, String message, Long validatedBy) {
        try {
            com.ciblorgasport.eventservice.kafka.ResultatValideEvent evt = new com.ciblorgasport.eventservice.kafka.ResultatValideEvent();
            evt.setEventId(UUID.randomUUID());
            evt.setCompetitionId(competitionId);
            evt.setEpreuveId(epreuveId);
            evt.setMessage(message);
            evt.setValidatedAt(Instant.now());
            evt.setValidatedByUserId(validatedBy);

            String payload = objectMapper.writeValueAsString(evt);
            kafkaTemplate.send(resultatsTopic, competitionId != null ? competitionId.toString() : "", payload);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot publish resultat event", e);
        }
    }
}
