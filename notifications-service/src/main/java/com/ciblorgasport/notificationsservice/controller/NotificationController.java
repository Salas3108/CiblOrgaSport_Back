package com.ciblorgasport.notificationsservice.controller;

import com.ciblorgasport.notificationsservice.dto.NotificationDTO;
import com.ciblorgasport.notificationsservice.model.Notification;
import com.ciblorgasport.notificationsservice.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * GET /api/notifications/spectateur/{spectateurId}
     * Historique complet des notifications d'un spectateur (les plus récentes en premier).
     */
    @GetMapping("/spectateur/{spectateurId}")
    public List<NotificationDTO> getAllBySpectateur(@PathVariable Long spectateurId) {
        return notificationRepository
                .findByIdSpectateurOrderByDateEnvoiDesc(spectateurId)
                .stream()
                .map(NotificationDTO::from)
                .toList();
    }

    /**
     * GET /api/notifications/spectateur/{spectateurId}/non-lues
     * Uniquement les notifications non lues.
     */
    @GetMapping("/spectateur/{spectateurId}/non-lues")
    public List<NotificationDTO> getUnreadBySpectateur(@PathVariable Long spectateurId) {
        return notificationRepository
                .findByIdSpectateurAndLuFalseOrderByDateEnvoiDesc(spectateurId)
                .stream()
                .map(NotificationDTO::from)
                .toList();
    }

    /**
     * GET /api/notifications/spectateur/{spectateurId}/compteur
     * Nombre de notifications non lues (utile pour le badge dans l'UI).
     */
    @GetMapping("/spectateur/{spectateurId}/compteur")
    public Map<String, Long> countUnread(@PathVariable Long spectateurId) {
        long count = notificationRepository.countByIdSpectateurAndLuFalse(spectateurId);
        return Map.of("nonLues", count);
    }

    /**
     * PATCH /api/notifications/{id}/lue
     * Marquer une notification comme lue.
     */
    @PatchMapping("/{id}/lue")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long id) {
        return notificationRepository.findById(id)
                .map(notification -> {
                    notification.setLu(true);
                    return ResponseEntity.ok(NotificationDTO.from(notificationRepository.save(notification)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PATCH /api/notifications/spectateur/{spectateurId}/tout-lire
     * Marquer toutes les notifications d'un spectateur comme lues.
     */
    @PatchMapping("/spectateur/{spectateurId}/tout-lire")
    public Map<String, Integer> markAllAsRead(@PathVariable Long spectateurId) {
        List<Notification> unread = notificationRepository
                .findByIdSpectateurAndLuFalseOrderByDateEnvoiDesc(spectateurId);
        unread.forEach(n -> n.setLu(true));
        notificationRepository.saveAll(unread);
        return Map.of("mis_a_jour", unread.size());
    }

    /**
     * DELETE /api/notifications/{id}
     * Supprimer une notification.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!notificationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        notificationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
