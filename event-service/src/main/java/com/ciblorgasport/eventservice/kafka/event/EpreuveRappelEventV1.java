package com.ciblorgasport.eventservice.kafka.event;

import java.time.Instant;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpreuveRappelEventV1 {

    public static final String EVENT_TYPE_VALUE = "epreuve.rappel";
    public static final int EVENT_VERSION = 1;

    private String eventId;
    private String eventType;
    private int version;
    private Instant occurredAt;

    private Long epreuveId;
    private String nom;
    private LocalDateTime dateHeure;
    private Long competitionId;

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }

    public Long getEpreuveId() { return epreuveId; }
    public void setEpreuveId(Long epreuveId) { this.epreuveId = epreuveId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
}
