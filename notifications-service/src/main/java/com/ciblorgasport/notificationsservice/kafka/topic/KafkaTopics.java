package com.ciblorgasport.notificationsservice.kafka.topic;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String INCIDENT_TOPIC = "ciblorgasport.incident.v1";
    public static final String INCIDENT_DLQ_TOPIC = "ciblorgasport.incident.v1.dlq";

    public static final String EPREUVE_RAPPEL_TOPIC = "ciblorgasport.epreuve.rappel.v1";
    public static final String EPREUVE_RAPPEL_DLQ_TOPIC = "ciblorgasport.epreuve.rappel.v1.dlq";
}
