package com.ciblorgasport.volunteerservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class AvailabilityDTO {
    @NotNull(message = "Le jour de la semaine est obligatoire")
    private String dayOfWeek;
    
    @NotNull(message = "L'heure de début est obligatoire")
    private String startTime;
    
    @NotNull(message = "L'heure de fin est obligatoire")
    private String endTime;

    // Constructeur par défaut (nécessaire pour Jackson)
    public AvailabilityDTO() {
    }

    @JsonCreator
    public AvailabilityDTO(
            @JsonProperty("dayOfWeek") String dayOfWeek,
            @JsonProperty("startTime") String startTime,
            @JsonProperty("endTime") String endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

    // Setters
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}