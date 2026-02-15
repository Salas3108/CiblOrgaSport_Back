package com.ciblorgasport.realtimegateway.kafka.dto;

import java.time.Instant;
import java.util.UUID;

public class NotificationEvent {
    private UUID id;
    private NotificationType type;
    private String message;

    private TargetType targetType;
    private Long targetUserId;
    private UUID targetCompetitionId;

    private Instant createdAt;

    public NotificationEvent() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public UUID getTargetCompetitionId() {
        return targetCompetitionId;
    }

    public void setTargetCompetitionId(UUID targetCompetitionId) {
        this.targetCompetitionId = targetCompetitionId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
