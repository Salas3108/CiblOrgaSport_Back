package com.ciblorgasport.notificationsservice.kafka;

import com.ciblorgasport.notificationsservice.client.AbonnementServiceClient;
import com.ciblorgasport.notificationsservice.kafka.event.ResultatFinalizedEventV1;
import com.ciblorgasport.notificationsservice.kafka.topic.KafkaTopics;
import com.ciblorgasport.notificationsservice.model.Notification;
import com.ciblorgasport.notificationsservice.repository.NotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.datasource.url=jdbc:h2:mem:notifications-resultat;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
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
        KafkaTopics.RESULTAT_FINAL_TOPIC,
        KafkaTopics.RESULTAT_FINAL_DLQ_TOPIC,
        KafkaTopics.INCIDENT_TOPIC,
        KafkaTopics.INCIDENT_DLQ_TOPIC,
        KafkaTopics.EPREUVE_RAPPEL_TOPIC,
        KafkaTopics.EPREUVE_RAPPEL_DLQ_TOPIC
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ResultatKafkaFlowIntegrationTest {

    @Autowired
    private KafkaTemplate<String, ResultatFinalizedEventV1> kafkaTemplate;

    @MockBean
    private AbonnementServiceClient abonnementServiceClient;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAll();
    }

    @Test
    void happyPath_CreatesResultNotificationRows() {
        when(abonnementServiceClient.getSubscribersWithNotifications(anyLong()))
                .thenReturn(List.of(21L, 22L));

        ResultatFinalizedEventV1 event = buildPodiumEvent("evt-resultat-1", 3001L, 5001L);
        kafkaTemplate.send(KafkaTopics.RESULTAT_FINAL_TOPIC, "epreuve-3001", event);

        awaitTrue(() -> notificationsBySource("evt-resultat-1").size() == 2, "result notifications should be persisted");

        List<Notification> rows = notificationsBySource("evt-resultat-1");
        assertTrue(rows.stream().allMatch(n -> "RESULTAT_FINAL".equals(n.getType())));
        assertTrue(rows.stream().anyMatch(n -> n.getContenu().contains("🥇")));
    }

    @Test
    void duplicateMessage_DoesNotInsertTwice() {
        when(abonnementServiceClient.getSubscribersWithNotifications(anyLong()))
                .thenReturn(List.of(23L));

        ResultatFinalizedEventV1 event = buildDuelEvent("evt-resultat-dup", 3002L, 5002L);
        kafkaTemplate.send(KafkaTopics.RESULTAT_FINAL_TOPIC, "epreuve-3002", event);
        awaitTrue(() -> notificationsBySource("evt-resultat-dup").size() == 1, "first result event should persist");

        kafkaTemplate.send(KafkaTopics.RESULTAT_FINAL_TOPIC, "epreuve-3002", event);
        awaitTrue(() -> notificationsBySource("evt-resultat-dup").size() == 1, "duplicate result event should not create a second row");
    }

    private List<Notification> notificationsBySource(String sourceEventId) {
        return notificationRepository.findAll().stream()
                .filter(n -> sourceEventId.equals(n.getSourceEventId()))
                .toList();
    }

    private ResultatFinalizedEventV1 buildPodiumEvent(String eventId, Long epreuveId, Long competitionId) {
        ResultatFinalizedEventV1 event = new ResultatFinalizedEventV1();
        event.setEventId(eventId);
        event.setEventType(ResultatFinalizedEventV1.EVENT_TYPE_VALUE);
        event.setVersion(ResultatFinalizedEventV1.EVENT_VERSION);
        event.setOccurredAt(Instant.now());
        event.setCompetitionId(competitionId);
        event.setEpreuveId(epreuveId);
        event.setNomEpreuve("200m papillon");
        event.setDisplayMode("PODIUM_3");

        event.setLines(List.of(
                line(1, "Athlete 1", "1:52.11"),
                line(2, "Athlete 2", "1:52.88"),
                line(3, "Athlete 3", "1:53.02")
        ));
        return event;
    }

    private ResultatFinalizedEventV1 buildDuelEvent(String eventId, Long epreuveId, Long competitionId) {
        ResultatFinalizedEventV1 event = new ResultatFinalizedEventV1();
        event.setEventId(eventId);
        event.setEventType(ResultatFinalizedEventV1.EVENT_TYPE_VALUE);
        event.setVersion(ResultatFinalizedEventV1.EVENT_VERSION);
        event.setOccurredAt(Instant.now());
        event.setCompetitionId(competitionId);
        event.setEpreuveId(epreuveId);
        event.setNomEpreuve("Water-polo demi-finale");
        event.setDisplayMode("DUEL_2");

        event.setLines(List.of(
                line(1, "Equipe A", "12"),
                line(2, "Equipe B", "10")
        ));
        return event;
    }

    private ResultatFinalizedEventV1.ResultLine line(Integer classement, String participant, String performance) {
        ResultatFinalizedEventV1.ResultLine line = new ResultatFinalizedEventV1.ResultLine();
        line.setClassement(classement);
        line.setParticipant(participant);
        line.setPerformance(performance);
        return line;
    }

    private void awaitTrue(BooleanSupplier condition, String failureMessage) {
        long deadline = System.currentTimeMillis() + 15_000;
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
