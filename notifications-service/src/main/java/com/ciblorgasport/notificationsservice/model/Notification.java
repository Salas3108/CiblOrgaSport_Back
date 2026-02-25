package com.ciblorgasport.notificationsservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "notification",
    uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(
            name = "uk_notification_source_event_recipient",
            columnNames = {"source_event_id", "id_spectateur"}
        )
    }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Long idNotification;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "contenu", nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @Column(name = "id_event")
    private Long idEvent;

    @Column(name = "id_spectateur")
    private Long idSpectateur;

    @Column(name = "source_event_id")
    private String sourceEventId;

    public Long getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(Long idNotification) {
        this.idNotification = idNotification;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public Long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Long idEvent) {
        this.idEvent = idEvent;
    }

    public Long getIdSpectateur() {
        return idSpectateur;
    }

    public void setIdSpectateur(Long idSpectateur) {
        this.idSpectateur = idSpectateur;
    }

    public String getSourceEventId() {
        return sourceEventId;
    }

    public void setSourceEventId(String sourceEventId) {
        this.sourceEventId = sourceEventId;
    }
}
