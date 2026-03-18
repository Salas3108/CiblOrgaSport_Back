package com.ciblorgasport.eventservice.kafka.publisher;

import com.ciblorgasport.eventservice.kafka.event.EpreuveRappelEventV1;
import com.ciblorgasport.eventservice.kafka.topic.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEpreuveEventPublisher implements EpreuveEventPublisher {

    private final KafkaTemplate<String, EpreuveRappelEventV1> kafkaTemplate;

    public KafkaEpreuveEventPublisher(KafkaTemplate<String, EpreuveRappelEventV1> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishEpreuveRappel(EpreuveRappelEventV1 event, String key) {
        kafkaTemplate.send(KafkaTopics.EPREUVE_RAPPEL_TOPIC, key, event);
    }
}
