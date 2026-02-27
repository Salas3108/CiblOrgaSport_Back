package com.ciblorgasport.notificationsservice.service;

import com.ciblorgasport.notificationsservice.dto.NotificationDTO;
import com.ciblorgasport.notificationsservice.kafka.event.IncidentCreatedEventV1;
import com.ciblorgasport.notificationsservice.model.Notification;
import com.ciblorgasport.notificationsservice.repository.AbonnementRepository;
import com.ciblorgasport.notificationsservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(NotificationGeneratorService.class);

    private final AbonnementRepository abonnementRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationGeneratorService(AbonnementRepository abonnementRepository,
                                        NotificationRepository notificationRepository,
                                        SimpMessagingTemplate messagingTemplate) {
        this.abonnementRepository = abonnementRepository;
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void handleIncidentCreated(IncidentCreatedEventV1 event) {
        if (event == null) {
            return;
        }

        Long competitionId = event.getCompetitionId();
        if (competitionId == null) {
            return;
        }

        String sourceEventId = resolveSourceEventId(event);
        if (sourceEventId == null) {
            log.warn("IncidentCreatedEvent has no idempotency key (eventId/incidentId missing); skipping");
            return;
        }

        List<Long> spectatorIds = abonnementRepository
                .findByIdIdCompetitionAndPreferenceNotifTrue(competitionId)
                .stream()
                .map(a -> a.getId().getIdSpectateur())
                .toList();

        Set<Long> recipientIds = new HashSet<>(spectatorIds);
        recipientIds.addAll(determineVolunteerRecipients(event));

        if (recipientIds.isEmpty()) {
            return;
        }

        List<Long> recipientIdList = List.copyOf(recipientIds);
        List<Long> alreadyNotified = notificationRepository
                .findRecipientIdsBySourceEventIdAndIdSpectateurIn(sourceEventId, recipientIdList);

        Set<Long> alreadyNotifiedSet = new HashSet<>(alreadyNotified);
        List<Long> toNotify = recipientIdList.stream()
                .filter(id -> !alreadyNotifiedSet.contains(id))
                .toList();

        if (toNotify.isEmpty()) {
            log.debug("Idempotence: event {} already processed for all recipients", sourceEventId);
            return;
        }

        String content = buildContent(event);
        LocalDateTime now = LocalDateTime.now();

        List<Notification> notifications = toNotify.stream().map(recipientId -> {
            Notification notification = new Notification();
            notification.setType("INCIDENT");
            notification.setContenu(content);
            notification.setDateEnvoi(now);
            notification.setIdEvent(competitionId);
            notification.setIdSpectateur(recipientId);
            notification.setSourceEventId(sourceEventId);
            return notification;
        }).collect(Collectors.toList());

        notificationRepository.saveAll(notifications);

        // Push temps réel via WebSocket : chaque spectateur reçoit sa notification
        // sur son topic personnel /topic/notifications/{spectateurId}
        notifications.forEach(saved ->
                messagingTemplate.convertAndSend(
                        "/topic/notifications/" + saved.getIdSpectateur(),
                        NotificationDTO.from(saved)
                )
        );
    }

    private String resolveSourceEventId(IncidentCreatedEventV1 event) {
        if (event.getEventId() != null && !event.getEventId().isBlank()) {
            return event.getEventId();
        }
        if (event.getIncidentId() != null) {
            return "incident-" + event.getIncidentId();
        }
        return null;
    }

    private List<Long> determineVolunteerRecipients(IncidentCreatedEventV1 event) {
        return List.of();
    }

    private String buildContent(IncidentCreatedEventV1 event) {
        String base = "Nouvel incident";
        if (event.getIncidentType() != null) {
            base += " (" + event.getIncidentType() + ")";
        }
        if (event.getLocation() != null) {
            base += " à " + event.getLocation();
        }
        if (event.getDescription() != null) {
            base += ": " + event.getDescription();
        }
        return base;
    }
}
