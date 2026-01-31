package com.ciblorgasport.resultatservice.dto;

import jakarta.validation.constraints.*;

public class ModifierResultatRequest {
    
    @Positive(message = "Le classement doit être positif")
    private Integer classement;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Le temps ne peut pas être négatif")
    private Double temps;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "La distance ne peut pas être négative")
    private Double distance;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Les points ne peuvent pas être négatifs")
    private Double points;
    
    @Size(max = 1000, message = "Les observations ne doivent pas dépasser 1000 caractères")
    private String observations;
    
    // Getters and Setters
    public Integer getClassement() {
        return classement;
    }
    
    public void setClassement(Integer classement) {
        this.classement = classement;
    }
    
    public Double getTemps() {
        return temps;
    }
    
    public void setTemps(Double temps) {
        this.temps = temps;
    }
    
    public Double getDistance() {
        return distance;
    }
    
    public void setDistance(Double distance) {
        this.distance = distance;
    }
    
    public Double getPoints() {
        return points;
    }
    
    public void setPoints(Double points) {
        this.points = points;
    }
    
    public String getObservations() {
        return observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
}