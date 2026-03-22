package com.ciblorgasport.notificationsservice.kafka.event;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultatFinalizedEventV1 {

    public static final String EVENT_TYPE_VALUE = "resultat.finalized";
    public static final int EVENT_VERSION = 1;

    private String eventId;
    private String eventType;
    private int version;
    private Instant occurredAt;

    private Long competitionId;
    private Long epreuveId;
    private String nomEpreuve;
    private String displayMode;

    private List<ResultLine> lines = new ArrayList<>();

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Long getEpreuveId() {
        return epreuveId;
    }

    public void setEpreuveId(Long epreuveId) {
        this.epreuveId = epreuveId;
    }

    public String getNomEpreuve() {
        return nomEpreuve;
    }

    public void setNomEpreuve(String nomEpreuve) {
        this.nomEpreuve = nomEpreuve;
    }

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public List<ResultLine> getLines() {
        return lines;
    }

    public void setLines(List<ResultLine> lines) {
        this.lines = lines;
    }

    public static class ResultLine {
        private Integer classement;
        private String participant;
        private String performance;

        public Integer getClassement() {
            return classement;
        }

        public void setClassement(Integer classement) {
            this.classement = classement;
        }

        public String getParticipant() {
            return participant;
        }

        public void setParticipant(String participant) {
            this.participant = participant;
        }

        public String getPerformance() {
            return performance;
        }

        public void setPerformance(String performance) {
            this.performance = performance;
        }
    }
}
