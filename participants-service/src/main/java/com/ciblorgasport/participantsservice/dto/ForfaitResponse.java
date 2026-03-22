package com.ciblorgasport.participantsservice.dto;

import java.time.LocalDateTime;

public class ForfaitResponse {

    private Long epreuveId;
    private Long athleteId;
    private String statutParticipation;
    private LocalDateTime dateForfait;
    private String detailsPerformance;
    private String message;

    public Long getEpreuveId() {
        return epreuveId;
    }

    public void setEpreuveId(Long epreuveId) {
        this.epreuveId = epreuveId;
    }

    public Long getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(Long athleteId) {
        this.athleteId = athleteId;
    }

    public String getStatutParticipation() {
        return statutParticipation;
    }

    public void setStatutParticipation(String statutParticipation) {
        this.statutParticipation = statutParticipation;
    }

    public LocalDateTime getDateForfait() {
        return dateForfait;
    }

    public void setDateForfait(LocalDateTime dateForfait) {
        this.dateForfait = dateForfait;
    }

    public String getDetailsPerformance() {
        return detailsPerformance;
    }

    public void setDetailsPerformance(String detailsPerformance) {
        this.detailsPerformance = detailsPerformance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
