package com.ciblorgasport.eventservice.kafka.topic;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String EPREUVE_RAPPEL_TOPIC = "ciblorgasport.epreuve.rappel.v1";
    public static final String EPREUVE_RAPPEL_DLQ_TOPIC = "ciblorgasport.epreuve.rappel.v1.dlq";
}
