package com.ciblorgasport.notificationsservice.kafka;

import com.ciblorgasport.notificationsservice.client.AbonnementServiceClient;
import com.ciblorgasport.notificationsservice.kafka.event.IncidentCreatedEventV1;
import com.ciblorgasport.notificationsservice.kafka.topic.KafkaTopics;
import com.ciblorgasport.notificationsservice.model.Notification;
import com.ciblorgasport.notificationsservice.repository.NotificationRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.datasource.url=jdbc:h2:mem:notifications;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.listener.concurrency=1",
        "spring.kafka.listener.missing-topics-fatal=false",
        "abonnement-service.url=http://localhost:8085"
})
@EmbeddedKafka(partitions = 1, topics = {
        KafkaTopics.INCIDENT_TOPIC,
        KafkaTopics.INCIDENT_DLQ_TOPIC,
        KafkaTopics.EPREUVE_RAPPEL_TOPIC,
        KafkaTopics.EPREUVE_RAPPEL_DLQ_TOPIC,
        KafkaTopics.RESULTAT_FINAL_TOPIC,
        KafkaTopics.RESULTAT_FINAL_DLQ_TOPIC
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IncidentKafkaFlowIntegrationTest {

    @Autowired
    private KafkaTemplate<String, IncidentCreatedEventV1> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockBean
    private AbonnementServiceClient abonnementServiceClient;

    @Autowired
    private NotificationRepository notificationRepository;

    @SpyBean
    private NotificationRepository notificationRepositorySpy;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAll();
    }

    @Test
    void happyPath_CreatesNotificationRows() {
        when(abonnementServiceClient.getSubscribersWithNotifications(anyLong()))
                .thenReturn(List.of(11L));

        IncidentCreatedEventV1 event = buildEvent("evt-happy-1", 7001L, 1001L);
        kafkaTemplate.send(KafkaTopics.INCIDENT_TOPIC, "inc-7001", event);

        awaitTrue(() -> notificationsBySource("evt-happy-1").size() == 1, "notification should be persisted");

        Notification notification = notificationsBySource("evt-happy-1").get(0);
        assertEquals(11L, notification.getIdSpectateur());
        assertEquals("INCIDENT", notification.getType());
        assertNotNull(notification.getDateEnvoi());
    }

    @Test
    void duplicateMessage_DoesNotInsertTwice() {
        when(abonnementServiceClient.getSubscribersWithNotifications(anyLong()))
                .thenReturn(List.of(12L));

        IncidentCreatedEventV1 event = buildEvent("evt-dup-1", 7002L, 1002L);
        kafkaTemplate.send(KafkaTopics.INCIDENT_TOPIC, "inc-7002", event);
        awaitTrue(() -> notificationsBySource("evt-dup-1").size() == 1, "first event should be persisted");

        kafkaTemplate.send(KafkaTopics.INCIDENT_TOPIC, "inc-7002", event);
        awaitTrue(() -> notificationsBySource("evt-dup-1").size() == 1, "duplicate event should not create new row");
    }

    @Test
    @SuppressWarnings("unchecked")
    void retry_OnTemporaryDbError_EventuallySucceeds() {
        when(abonnementServiceClient.getSubscribersWithNotifications(anyLong()))
                .thenReturn(List.of(13L));

        // Use an in-memory store: callRealMethod() is not supported on JPA interface proxy spies
        List<Notification> inMemoryStore = new CopyOnWriteArrayList<>();

        when(notificationRepositorySpy.findAll())
                .thenAnswer(inv -> new ArrayList<>(inMemoryStore));

        when(notificationRepositorySpy.findRecipientIdsBySourceEventIdAndIdSpectateurIn(anyString(), any()))
                .thenAnswer(inv -> {
                    String sourceId = inv.getArgument(0);
                    List<Long> recipientIds = inv.getArgument(1);
                    return inMemoryStore.stream()
                            .filter(n -> sourceId.equals(n.getSourceEventId())
                                    && recipientIds.contains(n.getIdSpectateur()))
                            .map(Notification::getIdSpectateur)
                            .toList();
                });

        AtomicInteger attempts = new AtomicInteger(0);
        doAnswer(invocation -> {
            int current = attempts.incrementAndGet();
            if (current <= 2) {
                throw new RuntimeException("simulated transient DB failure");
            }
            List<Notification> toSave = (List<Notification>) invocation.getArgument(0);
            List<Notification> saved = new ArrayList<>(toSave);
            inMemoryStore.addAll(saved);
            return saved;
        }).when(notificationRepositorySpy).saveAll(any());

        IncidentCreatedEventV1 event = buildEvent("evt-retry-1", 7003L, 1003L);
        kafkaTemplate.send(KafkaTopics.INCIDENT_TOPIC, "inc-7003", event);

        awaitTrue(() -> notificationsBySource("evt-retry-1").size() == 1, "message should succeed after retries");
        assertTrue(attempts.get() >= 3);
    }

    @Test
    void invalidMessage_IsSentToDlq() {
        Consumer<String, byte[]> dlqConsumer = createDlqConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(dlqConsumer, KafkaTopics.INCIDENT_DLQ_TOPIC);

        sendInvalidJson("bad-incident-key", "not-a-json-payload");

        ConsumerRecord<String, byte[]> dlqRecord = KafkaTestUtils.getSingleRecord(
                dlqConsumer,
                KafkaTopics.INCIDENT_DLQ_TOPIC,
                Duration.ofSeconds(25)
        );

        assertEquals("bad-incident-key", dlqRecord.key());
        assertNotNull(dlqRecord.value());

        dlqConsumer.close();
    }

    private Consumer<String, byte[]> createDlqConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("notifications-dlq-test", "true", embeddedKafkaBroker);
        consumerProps.put("auto.offset.reset", "earliest");
        return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new ByteArrayDeserializer())
                .createConsumer();
    }

    private void sendInvalidJson(String key, String payload) {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        Producer<String, String> producer = new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), new StringSerializer())
                .createProducer();
        try {
            producer.send(new ProducerRecord<>(KafkaTopics.INCIDENT_TOPIC, key, payload));
            producer.flush();
        } finally {
            producer.close();
        }
    }

    private List<Notification> notificationsBySource(String sourceEventId) {
        return notificationRepository.findAll().stream()
                .filter(n -> sourceEventId.equals(n.getSourceEventId()))
                .toList();
    }

    private IncidentCreatedEventV1 buildEvent(String eventId, Long incidentId, Long competitionId) {
        IncidentCreatedEventV1 event = new IncidentCreatedEventV1();
        event.setEventId(eventId);
        event.setEventType(IncidentCreatedEventV1.EVENT_TYPE_VALUE);
        event.setVersion(IncidentCreatedEventV1.EVENT_VERSION);
        event.setOccurredAt(Instant.now());
        event.setIncidentId(incidentId);
        event.setIncidentType("TECHNIQUE");
        event.setImpactLevel("MOYEN");
        event.setStatus("ACTIF");
        event.setDescription("Incident test");
        event.setLieuId(101L);
        event.setReportedBy("system");
        event.setCompetitionId(competitionId);
        return event;
    }

    private void awaitTrue(BooleanSupplier condition, String failureMessage) {
        long deadline = System.currentTimeMillis() + 60_000;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) {
                return;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("test interrupted", ex);
            }
        }
        throw new AssertionError(failureMessage);
    }
}
