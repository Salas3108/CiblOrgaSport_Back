package com.ciblorgasport.geolocationservice.dto;

import com.ciblorgasport.geolocationservice.entity.enums.TypeService;
import java.util.List;

public class FanZoneResponse {

    private Long id;
    private String nom;
    private String description;
    private Double latitude;
    private Double longitude;
    private Integer capaciteMax;
    private String adresse;
    private List<TypeService> services;
    private Double distance; // en mètres, rempli pour les recherches /nearby

    public FanZoneResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(Integer capaciteMax) { this.capaciteMax = capaciteMax; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public List<TypeService> getServices() { return services; }
    public void setServices(List<TypeService> services) { this.services = services; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
}
