package com.ciblorgasport.resultatsservice.kafka.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ciblorgasport.resultatsservice.kafka.event.ResultatFinalizedEventV1;
import com.ciblorgasport.resultatsservice.kafka.topic.KafkaTopics;

@Component
public class KafkaResultatEventPublisher implements ResultatEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaResultatEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishResultatFinalized(ResultatFinalizedEventV1 event, String key) {
        kafkaTemplate.send(KafkaTopics.RESULTAT_FINAL_TOPIC, key, event);
    }
}
