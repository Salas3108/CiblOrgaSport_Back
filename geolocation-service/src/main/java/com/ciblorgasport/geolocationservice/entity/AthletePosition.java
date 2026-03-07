package com.ciblorgasport.geolocationservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "athlete_position", indexes = {
        @Index(name = "idx_athlete_position_athlete_id", columnList = "athlete_id"),
        @Index(name = "idx_athlete_position_epreuve_id", columnList = "epreuve_id")
})
public class AthletePosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "athlete_id", nullable = false)
    private Long athleteId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "epreuve_id", nullable = false)
    private Long epreuveId;

    public AthletePosition() {}

    public AthletePosition(Long athleteId, Double latitude, Double longitude, LocalDateTime timestamp, Long epreuveId) {
        this.athleteId = athleteId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.epreuveId = epreuveId;
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

    public Long getEpreuveId() { return epreuveId; }
    public void setEpreuveId(Long epreuveId) { this.epreuveId = epreuveId; }
}
