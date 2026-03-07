package com.ciblorgasport.notificationsservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "abonnement")
public class Abonnement {

    @EmbeddedId
    private AbonnementId id;

    @Column(name = "date_abonnement")
    private LocalDateTime dateAbonnement;

    @Column(name = "preference_notif")
    private Boolean preferenceNotif;

    public AbonnementId getId() {
        return id;
    }

    public void setId(AbonnementId id) {
        this.id = id;
    }

    public LocalDateTime getDateAbonnement() {
        return dateAbonnement;
    }

    public void setDateAbonnement(LocalDateTime dateAbonnement) {
        this.dateAbonnement = dateAbonnement;
    }

    public Boolean getPreferenceNotif() {
        return preferenceNotif;
    }

    public void setPreferenceNotif(Boolean preferenceNotif) {
        this.preferenceNotif = preferenceNotif;
    }
}
