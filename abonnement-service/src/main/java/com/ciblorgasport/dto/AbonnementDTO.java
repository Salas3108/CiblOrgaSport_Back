package com.ciblorgasport.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ciblorgasport.entity.AbonnementStatus;

public class AbonnementDTO {
    private UUID id;
    private Long userId;
    private Long competitionId;
    private LocalDateTime dateAbonnement;
    private boolean notificationsActives;
    private AbonnementStatus status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }

    public LocalDateTime getDateAbonnement() { return dateAbonnement; }
    public void setDateAbonnement(LocalDateTime dateAbonnement) { this.dateAbonnement = dateAbonnement; }

    public boolean isNotificationsActives() { return notificationsActives; }
    public void setNotificationsActives(boolean notificationsActives) { this.notificationsActives = notificationsActives; }

    public AbonnementStatus getStatus() { return status; }
    public void setStatus(AbonnementStatus status) { this.status = status; }
}
