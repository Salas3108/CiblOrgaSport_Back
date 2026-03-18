package com.ciblorgasport.notificationsservice.dto;

import com.ciblorgasport.notificationsservice.model.Notification;

import java.time.LocalDateTime;

public class NotificationDTO {

    private Long id;
    private String type;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private Long idEvent;
    private Long idSpectateur;
    private String sourceEventId;
    private boolean lu;

    public NotificationDTO() {
    }

    public static NotificationDTO from(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.id = n.getIdNotification();
        dto.type = n.getType();
        dto.contenu = n.getContenu();
        dto.dateEnvoi = n.getDateEnvoi();
        dto.idEvent = n.getIdEvent();
        dto.idSpectateur = n.getIdSpectateur();
        dto.sourceEventId = n.getSourceEventId();
        dto.lu = n.isLu();
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public Long getIdEvent() { return idEvent; }
    public void setIdEvent(Long idEvent) { this.idEvent = idEvent; }

    public Long getIdSpectateur() { return idSpectateur; }
    public void setIdSpectateur(Long idSpectateur) { this.idSpectateur = idSpectateur; }

    public String getSourceEventId() { return sourceEventId; }
    public void setSourceEventId(String sourceEventId) { this.sourceEventId = sourceEventId; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }
}
