package com.ciblorgasport.geolocationservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "athlete_geo_config")
public class AthleteGeoConfig {

    @Id
    @Column(name = "athlete_id")
    private Long athleteId;

    @Column(name = "geoloc_active", nullable = false)
    private boolean geolocActive = false;

    @Column(name = "nom")
    private String nom;

    public AthleteGeoConfig() {}

    public AthleteGeoConfig(Long athleteId, boolean geolocActive, String nom) {
        this.athleteId = athleteId;
        this.geolocActive = geolocActive;
        this.nom = nom;
    }

    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }

    public boolean isGeolocActive() { return geolocActive; }
    public void setGeolocActive(boolean geolocActive) { this.geolocActive = geolocActive; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
