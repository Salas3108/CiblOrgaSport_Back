package com.ciblorgasport.notificationsservice.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.ciblorgasport.notificationsservice.client.AbonnementServiceClient;
import com.ciblorgasport.notificationsservice.dto.NotificationDTO;
import com.ciblorgasport.notificationsservice.kafka.event.IncidentCreatedEventV1;
import com.ciblorgasport.notificationsservice.model.Notification;
import com.ciblorgasport.notificationsservice.repository.NotificationRepository;

@Service
public class NotificationGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(NotificationGeneratorService.class);

    private final AbonnementServiceClient abonnementServiceClient;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationGeneratorService(AbonnementServiceClient abonnementServiceClient,
                                        NotificationRepository notificationRepository,
                                        SimpMessagingTemplate messagingTemplate) {
        this.abonnementServiceClient = abonnementServiceClient;
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

        // Fetch subscribers from abonnement-service via HTTP.
        List<Long> spectatorIds = abonnementServiceClient
                .getSubscribersWithNotifications(competitionId);

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

        List<Notification> saved = notificationRepository.saveAll(notifications);
        log.debug("Persisted {} notification(s) for sourceEventId={}", saved.size(), sourceEventId);

        // Push WebSocket APRÈS le commit de la transaction
        // (évite d'envoyer si la transaction rollback)
        List<NotificationDTO> dtos = saved.stream().map(NotificationDTO::from).toList();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                dtos.forEach(dto -> {
                    log.debug("WS push → /topic/notifications/{} notifId={}", dto.getIdSpectateur(), dto.getId());
                    messagingTemplate.convertAndSend(
                            "/topic/notifications/" + dto.getIdSpectateur(),
                            dto
                    );
                });
            }
        });
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
