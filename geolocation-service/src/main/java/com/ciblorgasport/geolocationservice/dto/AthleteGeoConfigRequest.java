package com.ciblorgasport.geolocationservice.dto;

public class AthleteGeoConfigRequest {

    private boolean geolocActive;
    private String nom;

    public boolean isGeolocActive() { return geolocActive; }
    public void setGeolocActive(boolean geolocActive) { this.geolocActive = geolocActive; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
