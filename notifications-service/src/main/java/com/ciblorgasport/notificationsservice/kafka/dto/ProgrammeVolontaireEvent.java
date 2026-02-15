package com.ciblorgasport.notificationsservice.kafka.dto;

import java.time.Instant;
import java.util.UUID;

public class ProgrammeVolontaireEvent {
    private UUID competitionId;
    private String message;
    private Instant publishedAt;

    public ProgrammeVolontaireEvent() {
    }

    public UUID getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(UUID competitionId) {
        this.competitionId = competitionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
}
