package com.ciblorgasport.incidentservice.kafka.topic;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String INCIDENT_TOPIC = "ciblorgasport.incident.v1";
    public static final String INCIDENT_DLQ_TOPIC = "ciblorgasport.incident.v1.dlq";
}
