package com.ciblorgasport.resultatservice.dto;

import com.ciblorgasport.resultatservice.entity.StatusResultat;
import jakarta.validation.constraints.*;

public class CreerResultatRequest {
    
    @NotNull(message = "L'ID de l'épreuve est requis")
    private Long epreuveId;
    
    @NotNull(message = "L'ID de l'athlète est requis")
    private Long athleteId;
    
    @NotNull(message = "Le classement est requis")
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
    
    // Constructors
    public CreerResultatRequest() {}
    
    public CreerResultatRequest(Long epreuveId, Long athleteId, Integer classement, 
                               Double temps, Double distance, Double points, String observations) {
        this.epreuveId = epreuveId;
        this.athleteId = athleteId;
        this.classement = classement;
        this.temps = temps;
        this.distance = distance;
        this.points = points;
        this.observations = observations;
    }
}