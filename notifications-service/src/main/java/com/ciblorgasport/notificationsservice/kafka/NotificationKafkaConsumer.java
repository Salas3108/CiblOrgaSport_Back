package com.ciblorgasport.notificationsservice.kafka;

import com.ciblorgasport.notificationsservice.kafka.dto.*;
import com.ciblorgasport.notificationsservice.notification.entity.Notification;
import com.ciblorgasport.notificationsservice.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class NotificationKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;
    private final NotificationEventPublisher notificationEventPublisher;

    private final String resultatsTopic;
    private final String incidentsTopic;
    private final String programmeTopic;

    public NotificationKafkaConsumer(ObjectMapper objectMapper,
                                    NotificationRepository notificationRepository,
                                    NotificationEventPublisher notificationEventPublisher,
                                    @Value("${ciblorgasport.kafka.topics.resultats}") String resultatsTopic,
                                    @Value("${ciblorgasport.kafka.topics.incidents}") String incidentsTopic,
                                    @Value("${ciblorgasport.kafka.topics.programme}") String programmeTopic) {
        this.objectMapper = objectMapper;
        this.notificationRepository = notificationRepository;
        this.notificationEventPublisher = notificationEventPublisher;
        this.resultatsTopic = resultatsTopic;
        this.incidentsTopic = incidentsTopic;
        this.programmeTopic = programmeTopic;
    }

    @KafkaListener(topics = "${ciblorgasport.kafka.topics.resultats}")
    public void onResultat(String payload) throws Exception {
        ResultatValideEvent event = objectMapper.readValue(payload, ResultatValideEvent.class);
        NotificationEvent notif = new NotificationEvent();
        notif.setId(UUID.randomUUID());
        notif.setType(NotificationType.RESULTAT);
        notif.setMessage(event.getMessage() != null ? event.getMessage() : "Résultat validé");
        notif.setTargetType(TargetType.COMPETITION_SUBSCRIBERS);
        notif.setTargetCompetitionId(event.getCompetitionId());
        notif.setCreatedAt(Instant.now());

        persist(notif);
        notificationEventPublisher.publish(notif);
    }

    @KafkaListener(topics = "${ciblorgasport.kafka.topics.incidents}")
    public void onIncident(String payload) throws Exception {
        IncidentEvent event = objectMapper.readValue(payload, IncidentEvent.class);
        NotificationEvent notif = new NotificationEvent();
        notif.setId(UUID.randomUUID());
        notif.setType(NotificationType.INCIDENT);
        String msg = (event.getDescription() != null && !event.getDescription().isBlank())
                ? event.getDescription()
                : "Incident sécurité";
        notif.setMessage(msg);
        notif.setTargetType(TargetType.BROADCAST);
        notif.setCreatedAt(Instant.now());

        persist(notif);
        notificationEventPublisher.publish(notif);
    }

    @KafkaListener(topics = "${ciblorgasport.kafka.topics.programme}")
    public void onProgramme(String payload) throws Exception {
        ProgrammeVolontaireEvent event = objectMapper.readValue(payload, ProgrammeVolontaireEvent.class);
        NotificationEvent notif = new NotificationEvent();
        notif.setId(UUID.randomUUID());
        notif.setType(NotificationType.PROGRAMME);
        notif.setMessage(event.getMessage() != null ? event.getMessage() : "Programme des volontaires publié");
        notif.setTargetType(TargetType.COMPETITION_SUBSCRIBERS);
        notif.setTargetCompetitionId(event.getCompetitionId());
        notif.setCreatedAt(Instant.now());

        persist(notif);
        notificationEventPublisher.publish(notif);
    }

    private void persist(NotificationEvent notif) {
        Notification entity = new Notification();
        entity.setType(notif.getType());
        entity.setMessage(notif.getMessage());
        entity.setTargetType(notif.getTargetType());
        entity.setTargetUserId(notif.getTargetUserId());
        entity.setTargetCompetitionId(notif.getTargetCompetitionId());
        entity.setCreatedAt(notif.getCreatedAt() != null ? notif.getCreatedAt() : Instant.now());
        notificationRepository.save(entity);
    }
}
