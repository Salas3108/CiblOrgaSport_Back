package com.ciblorgasport.geolocationservice.dto;

import java.time.LocalDateTime;

public class WebSocketPositionMessage {

    private Long athleteId;
    private String nom;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private Long epreuveId;

    public WebSocketPositionMessage() {}

    public WebSocketPositionMessage(Long athleteId, String nom, Double latitude, Double longitude,
                                    LocalDateTime timestamp, Long epreuveId) {
        this.athleteId = athleteId;
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.epreuveId = epreuveId;
    }

    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getEpreuveId() { return epreuveId; }
    public void setEpreuveId(Long epreuveId) { this.epreuveId = epreuveId; }
}
