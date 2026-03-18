package com.ciblorgasport.geolocationservice.dto;

import java.time.LocalDateTime;

public class WebSocketPositionMessage {

    private Long athleteId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;

    public WebSocketPositionMessage() {}

    public WebSocketPositionMessage(Long athleteId, Double latitude, Double longitude, LocalDateTime timestamp) {
        this.athleteId = athleteId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
