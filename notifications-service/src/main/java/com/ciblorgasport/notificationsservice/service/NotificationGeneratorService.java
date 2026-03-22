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
import com.ciblorgasport.notificationsservice.kafka.event.EpreuveRappelEventV1;
import com.ciblorgasport.notificationsservice.kafka.event.IncidentCreatedEventV1;
import com.ciblorgasport.notificationsservice.kafka.event.ResultatFinalizedEventV1;
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
        if (event.getLieuId() != null) {
            base += " (lieuId=" + event.getLieuId() + ")";
        }
        if (event.getDescription() != null) {
            base += ": " + event.getDescription();
        }
        return base;
    }

    // ---- Epreuve Rappel ----

    @Transactional
    public void handleEpreuveRappel(EpreuveRappelEventV1 event) {
        if (event == null) {
            return;
        }

        Long competitionId = event.getCompetitionId();
        if (competitionId == null) {
            return;
        }

        String sourceEventId = event.getEventId() != null && !event.getEventId().isBlank()
                ? event.getEventId()
                : "epreuve-rappel-" + event.getEpreuveId();

        List<Long> spectatorIds = abonnementServiceClient.getSubscribersWithNotifications(competitionId);

        if (spectatorIds.isEmpty()) {
            return;
        }

        List<Long> alreadyNotified = notificationRepository
                .findRecipientIdsBySourceEventIdAndIdSpectateurIn(sourceEventId, spectatorIds);

        Set<Long> alreadyNotifiedSet = new HashSet<>(alreadyNotified);
        List<Long> toNotify = spectatorIds.stream()
                .filter(id -> !alreadyNotifiedSet.contains(id))
                .toList();

        if (toNotify.isEmpty()) {
            log.debug("Idempotence: epreuve rappel event {} already processed for all recipients", sourceEventId);
            return;
        }

        String content = buildEpreuveRappelContent(event);
        LocalDateTime now = LocalDateTime.now();

        List<Notification> notifications = toNotify.stream().map(recipientId -> {
            Notification notification = new Notification();
            notification.setType("EPREUVE_RAPPEL");
            notification.setContenu(content);
            notification.setDateEnvoi(now);
            notification.setIdEvent(competitionId);
            notification.setIdSpectateur(recipientId);
            notification.setSourceEventId(sourceEventId);
            return notification;
        }).collect(Collectors.toList());

        List<Notification> saved = notificationRepository.saveAll(notifications);
        log.debug("Persisted {} rappel notification(s) for sourceEventId={}", saved.size(), sourceEventId);

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

    private String buildEpreuveRappelContent(EpreuveRappelEventV1 event) {
        String base = "Rappel : l'épreuve";
        if (event.getNom() != null) {
            base += " \"" + event.getNom() + "\"";
        }
        if (event.getDateHeure() != null) {
            base += " aura lieu le " + event.getDateHeure().toLocalDate()
                    + " à " + event.getDateHeure().toLocalTime();
        }
        return base;
    }

    // ---- Resultat Final ----

    @Transactional
    public void handleResultatFinalized(ResultatFinalizedEventV1 event) {
        if (event == null) {
            return;
        }

        Long competitionId = event.getCompetitionId();
        if (competitionId == null) {
            return;
        }

        String sourceEventId = event.getEventId() != null && !event.getEventId().isBlank()
                ? event.getEventId()
                : "resultat-final-" + event.getEpreuveId();

        List<Long> spectatorIds = abonnementServiceClient.getSubscribersWithNotifications(competitionId);
        if (spectatorIds.isEmpty()) {
            return;
        }

        List<Long> alreadyNotified = notificationRepository
                .findRecipientIdsBySourceEventIdAndIdSpectateurIn(sourceEventId, spectatorIds);

        Set<Long> alreadyNotifiedSet = new HashSet<>(alreadyNotified);
        List<Long> toNotify = spectatorIds.stream()
                .filter(id -> !alreadyNotifiedSet.contains(id))
                .toList();

        if (toNotify.isEmpty()) {
            log.debug("Idempotence: resultat event {} already processed for all recipients", sourceEventId);
            return;
        }

        String content = buildResultatFinalContent(event);
        LocalDateTime now = LocalDateTime.now();

        List<Notification> notifications = toNotify.stream().map(recipientId -> {
            Notification notification = new Notification();
            notification.setType("RESULTAT_FINAL");
            notification.setContenu(content);
            notification.setDateEnvoi(now);
            notification.setIdEvent(competitionId);
            notification.setIdSpectateur(recipientId);
            notification.setSourceEventId(sourceEventId);
            return notification;
        }).collect(Collectors.toList());

        List<Notification> saved = notificationRepository.saveAll(notifications);
        log.debug("Persisted {} resultat notification(s) for sourceEventId={}", saved.size(), sourceEventId);

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

    private String buildResultatFinalContent(ResultatFinalizedEventV1 event) {
        String nomEpreuve = event.getNomEpreuve() != null && !event.getNomEpreuve().isBlank()
                ? event.getNomEpreuve()
                : "Epreuve";

        StringBuilder builder = new StringBuilder();
        builder.append("Resultats officiels - ").append(nomEpreuve).append("\n\n");

        List<ResultatFinalizedEventV1.ResultLine> lines = event.getLines() != null
                ? event.getLines()
                : List.of();

        if ("PODIUM_3".equals(event.getDisplayMode()) && lines.size() >= 3) {
            builder.append(formatMedalLine("🥇", lines.get(0))).append("\n");
            builder.append(formatMedalLine("🥈", lines.get(1))).append("\n");
            builder.append(formatMedalLine("🥉", lines.get(2)));
            return builder.toString();
        }

        if ("DUEL_2".equals(event.getDisplayMode()) && lines.size() >= 2) {
            builder.append("Score final\n");
            builder.append(formatPlainLine(lines.get(0))).append("\n");
            builder.append(formatPlainLine(lines.get(1)));
            return builder.toString();
        }

        if (!lines.isEmpty()) {
            builder.append(formatMedalLine("🥇", lines.get(0)));
            return builder.toString();
        }

        builder.append("Resultats disponibles.");
        return builder.toString();
    }

    private String formatMedalLine(String medal, ResultatFinalizedEventV1.ResultLine line) {
        return medal + " " + safeParticipant(line) + " - " + safePerformance(line);
    }

    private String formatPlainLine(ResultatFinalizedEventV1.ResultLine line) {
        return safeParticipant(line) + " - " + safePerformance(line);
    }

    private String safeParticipant(ResultatFinalizedEventV1.ResultLine line) {
        if (line == null || line.getParticipant() == null || line.getParticipant().isBlank()) {
            return "Participant inconnu";
        }
        return line.getParticipant();
    }

    private String safePerformance(ResultatFinalizedEventV1.ResultLine line) {
        if (line == null || line.getPerformance() == null || line.getPerformance().isBlank()) {
            return "N/A";
        }
        return line.getPerformance();
    }
}
