package com.ciblorgasport.incidentservice.kafka.publisher;

import com.ciblorgasport.incidentservice.kafka.event.IncidentCreatedEventV1;
import com.ciblorgasport.incidentservice.kafka.topic.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaIncidentEventPublisher implements IncidentEventPublisher {

    private final KafkaTemplate<String, IncidentCreatedEventV1> kafkaTemplate;

    public KafkaIncidentEventPublisher(KafkaTemplate<String, IncidentCreatedEventV1> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishIncidentCreated(IncidentCreatedEventV1 event, String key) {
        kafkaTemplate.send(KafkaTopics.INCIDENT_TOPIC, key, event);
    }
}
