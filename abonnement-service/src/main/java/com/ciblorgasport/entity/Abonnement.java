package com.ciblorgasport.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "abonnements", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "competition_id"}))
public class Abonnement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "competition_id", nullable = false)
    private UUID competitionId;

    @Column(name = "date_abonnement", nullable = false)
    private LocalDateTime dateAbonnement;

    @Column(name = "notifications_actives", nullable = false)
    private boolean notificationsActives = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AbonnementStatus status = AbonnementStatus.ACTIF;

    // Constructeur
    public Abonnement() {
        this.dateAbonnement = LocalDateTime.now();
    }

    public Abonnement(Long userId, UUID competitionId) {
        this();
        this.userId = userId;
        this.competitionId = competitionId;
    }

    // Getters et setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public UUID getCompetitionId() { return competitionId; }
    public void setCompetitionId(UUID competitionId) { this.competitionId = competitionId; }

    public LocalDateTime getDateAbonnement() { return dateAbonnement; }
    public void setDateAbonnement(LocalDateTime dateAbonnement) { this.dateAbonnement = dateAbonnement; }

    public boolean isNotificationsActives() { return notificationsActives; }
    public void setNotificationsActives(boolean notificationsActives) { this.notificationsActives = notificationsActives; }

    public AbonnementStatus getStatus() { return status; }
    public void setStatus(AbonnementStatus status) { this.status = status; }
}

