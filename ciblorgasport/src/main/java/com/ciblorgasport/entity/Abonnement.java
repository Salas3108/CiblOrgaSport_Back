package com.ciblorgasport.entity;


import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;


@Entity
@Table(name = "abonnements", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "competition_id"}))
public class Abonnement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "event", "epreuves"})
    private Competition competition;
    
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
    
    public Abonnement(Long userId, Competition competition) {
        this();
        this.userId = userId;
        this.competition = competition;
    }
    
    // Getters et setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    
    public LocalDateTime getDateAbonnement() { return dateAbonnement; }
    public void setDateAbonnement(LocalDateTime dateAbonnement) { this.dateAbonnement = dateAbonnement; }
    
    public boolean isNotificationsActives() { return notificationsActives; }
    public void setNotificationsActives(boolean notificationsActives) { this.notificationsActives = notificationsActives; }
    
    public AbonnementStatus getStatus() { return status; }
    public void setStatus(AbonnementStatus status) { this.status = status; }
}

enum AbonnementStatus {
    ACTIF,
    DESABONNE,
    SUSPENDU
}