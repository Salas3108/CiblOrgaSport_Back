package com.ciblorgasport.notificationsservice.kafka.config;

import com.ciblorgasport.notificationsservice.kafka.event.EpreuveRappelEventV1;
import com.ciblorgasport.notificationsservice.kafka.event.IncidentCreatedEventV1;
import com.ciblorgasport.notificationsservice.kafka.event.ResultatFinalizedEventV1;
import com.ciblorgasport.notificationsservice.kafka.topic.KafkaTopics;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Bean
    public ConsumerFactory<String, IncidentCreatedEventV1> incidentCreatedConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, IncidentCreatedEventV1.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ciblorgasport.notificationsservice.kafka.event");

        JsonDeserializer<IncidentCreatedEventV1> jsonDeserializer = new JsonDeserializer<>();
        ErrorHandlingDeserializer<IncidentCreatedEventV1> valueDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, IncidentCreatedEventV1> kafkaListenerContainerFactory(
            ConsumerFactory<String, IncidentCreatedEventV1> incidentCreatedConsumerFactory,
            DefaultErrorHandler kafkaErrorHandler,
            KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, IncidentCreatedEventV1> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(incidentCreatedConsumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        Integer concurrency = kafkaProperties.getListener().getConcurrency();
        if (concurrency != null) {
            factory.setConcurrency(concurrency);
        }
        return factory;
    }

    @Bean
    public ConsumerFactory<String, EpreuveRappelEventV1> epreuveRappelConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, EpreuveRappelEventV1.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ciblorgasport.notificationsservice.kafka.event");

        JsonDeserializer<EpreuveRappelEventV1> jsonDeserializer = new JsonDeserializer<>();
        ErrorHandlingDeserializer<EpreuveRappelEventV1> valueDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EpreuveRappelEventV1> epreuveRappelKafkaListenerContainerFactory(
            ConsumerFactory<String, EpreuveRappelEventV1> epreuveRappelConsumerFactory,
            DefaultErrorHandler epreuveRappelErrorHandler,
            KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, EpreuveRappelEventV1> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(epreuveRappelConsumerFactory);
        factory.setCommonErrorHandler(epreuveRappelErrorHandler);
        Integer concurrency = kafkaProperties.getListener().getConcurrency();
        if (concurrency != null) {
            factory.setConcurrency(concurrency);
        }
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ResultatFinalizedEventV1> resultatFinalConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ResultatFinalizedEventV1.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ciblorgasport.notificationsservice.kafka.event");

        JsonDeserializer<ResultatFinalizedEventV1> jsonDeserializer = new JsonDeserializer<>();
        ErrorHandlingDeserializer<ResultatFinalizedEventV1> valueDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ResultatFinalizedEventV1> resultatFinalKafkaListenerContainerFactory(
            ConsumerFactory<String, ResultatFinalizedEventV1> resultatFinalConsumerFactory,
            DefaultErrorHandler resultatFinalErrorHandler,
            KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, ResultatFinalizedEventV1> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(resultatFinalConsumerFactory);
        factory.setCommonErrorHandler(resultatFinalErrorHandler);
        Integer concurrency = kafkaProperties.getListener().getConcurrency();
        if (concurrency != null) {
            factory.setConcurrency(concurrency);
        }
        return factory;
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(KafkaTopics.INCIDENT_DLQ_TOPIC, record.partition()));

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
        errorHandler.addNotRetryableExceptions(DeserializationException.class);
        errorHandler.addNotRetryableExceptions(org.apache.kafka.common.errors.SerializationException.class);
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            if (record != null) {
                LOG.warn("Kafka consume failed (attempt {}), topic={}, partition={}, offset={}, key={}, reason={}",
                        deliveryAttempt,
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.key(),
                        ex != null ? ex.getMessage() : null);
            }
        });
        return errorHandler;
    }

    @Bean
    public DefaultErrorHandler epreuveRappelErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(KafkaTopics.EPREUVE_RAPPEL_DLQ_TOPIC, record.partition()));

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
        errorHandler.addNotRetryableExceptions(DeserializationException.class);
        errorHandler.addNotRetryableExceptions(org.apache.kafka.common.errors.SerializationException.class);
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            if (record != null) {
                LOG.warn("Kafka consume failed (attempt {}), topic={}, partition={}, offset={}, key={}, reason={}",
                        deliveryAttempt,
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.key(),
                        ex != null ? ex.getMessage() : null);
            }
        });
        return errorHandler;
    }

    @Bean
    public DefaultErrorHandler resultatFinalErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(KafkaTopics.RESULTAT_FINAL_DLQ_TOPIC, record.partition()));

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
        errorHandler.addNotRetryableExceptions(DeserializationException.class);
        errorHandler.addNotRetryableExceptions(org.apache.kafka.common.errors.SerializationException.class);
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            if (record != null) {
                LOG.warn("Kafka consume failed (attempt {}), topic={}, partition={}, offset={}, key={}, reason= {}",
                        deliveryAttempt,
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.key(),
                        ex != null ? ex.getMessage() : null);
            }
        });
        return errorHandler;
    }
}
