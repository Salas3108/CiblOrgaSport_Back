package com.ciblorgasport.incidentservice.kafka.config;

import com.ciblorgasport.incidentservice.kafka.topic.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic incidentCreatedTopic() {
        return TopicBuilder.name(KafkaTopics.INCIDENT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic incidentCreatedDlqTopic() {
        return TopicBuilder.name(KafkaTopics.INCIDENT_DLQ_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
