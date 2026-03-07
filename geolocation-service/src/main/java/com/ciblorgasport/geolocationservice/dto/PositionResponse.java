package com.ciblorgasport.geolocationservice.dto;

import java.time.LocalDateTime;

public class PositionResponse {

    private Long id;
    private Long athleteId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;

    public PositionResponse() {}

    public PositionResponse(Long id, Long athleteId, Double latitude, Double longitude, LocalDateTime timestamp) {
        this.id = id;
        this.athleteId = athleteId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
