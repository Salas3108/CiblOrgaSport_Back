package com.ciblorgasport.incidentservice.kafka.publisher;

import com.ciblorgasport.incidentservice.kafka.event.IncidentCreatedEventV1;
import com.ciblorgasport.incidentservice.kafka.topic.KafkaTopics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaIncidentEventPublisherTest {

    @Mock
    private KafkaTemplate<String, IncidentCreatedEventV1> kafkaTemplate;

    @Test
    void publishIncidentCreated_UsesUnifiedIncidentTopic() {
        KafkaIncidentEventPublisher publisher = new KafkaIncidentEventPublisher(kafkaTemplate);
        IncidentCreatedEventV1 event = new IncidentCreatedEventV1();

        publisher.publishIncidentCreated(event, "incident-key-1");

        verify(kafkaTemplate).send(KafkaTopics.INCIDENT_TOPIC, "incident-key-1", event);
    }
}
