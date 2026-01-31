package com.ciblorgasport.resultatservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_resultats")
public class HistoriqueResultat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long resultatId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusResultat ancienStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusResultat nouveauStatus;
    
    @Column(nullable = false)
    private Long modifiePar;
    
    @Column(columnDefinition = "TEXT")
    private String raison;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateModification;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getResultatId() {
        return resultatId;
    }
    
    public void setResultatId(Long resultatId) {
        this.resultatId = resultatId;
    }
    
    public StatusResultat getAncienStatus() {
        return ancienStatus;
    }
    
    public void setAncienStatus(StatusResultat ancienStatus) {
        this.ancienStatus = ancienStatus;
    }
    
    public StatusResultat getNouveauStatus() {
        return nouveauStatus;
    }
    
    public void setNouveauStatus(StatusResultat nouveauStatus) {
        this.nouveauStatus = nouveauStatus;
    }
    
    public Long getModifiePar() {
        return modifiePar;
    }
    
    public void setModifiePar(Long modifiePar) {
        this.modifiePar = modifiePar;
    }
    
    public String getRaison() {
        return raison;
    }
    
    public void setRaison(String raison) {
        this.raison = raison;
    }
    
    public LocalDateTime getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
    
    @PrePersist
    protected void onCreate() {
        dateModification = LocalDateTime.now();
    }
}