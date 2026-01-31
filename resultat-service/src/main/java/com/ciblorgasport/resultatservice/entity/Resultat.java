package com.ciblorgasport.resultatservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultats")
public class Resultat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long epreuveId;
    
    @Column(nullable = false)
    private Long athleteId;
    
    @Column(nullable = false)
    private Integer classement;
    
    @Column(columnDefinition = "NUMERIC(5,2)")
    private Double temps;
    
    @Column(columnDefinition = "NUMERIC(8,2)")
    private Double distance;
    
    @Column(columnDefinition = "NUMERIC(8,2)")
    private Double points;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusResultat status;
    
    @Column(nullable = false)
    private Long saisieParId;
    
    @Column(columnDefinition = "TEXT")
    private String observations;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateCreation;
    
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateModification;
    
    @Version
    private Long version;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public StatusResultat getStatus() {
        return status;
    }
    
    public void setStatus(StatusResultat status) {
        this.status = status;
    }
    
    public Long getSaisieParId() {
        return saisieParId;
    }
    
    public void setSaisieParId(Long saisieParId) {
        this.saisieParId = saisieParId;
    }
    
    public String getObservations() {
        return observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    // Callbacks
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        if (status == null) {
            status = StatusResultat.SAISI;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}