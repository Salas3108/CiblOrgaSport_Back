package com.ciblorgasport.eventservice.kafka.config;

import com.ciblorgasport.eventservice.kafka.topic.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic epreuveRappelTopic() {
        return TopicBuilder.name(KafkaTopics.EPREUVE_RAPPEL_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic epreuveRappelDlqTopic() {
        return TopicBuilder.name(KafkaTopics.EPREUVE_RAPPEL_DLQ_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
