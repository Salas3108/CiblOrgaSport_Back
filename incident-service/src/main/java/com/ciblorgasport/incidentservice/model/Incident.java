package com.ciblorgasport.incidentservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity(name = "IncidentModel")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;              // Description de l'incident

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImpactLevel impactLevel;         // FAIBLE, MOYEN, ELEVE, CRITIQUE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentType type;               // SECURITE, TECHNIQUE, METEO, MEDICAL, AUTRE

    @Column(nullable = false)
    private String location;                 // Lieu de l'incident

    private Long competitionId;              // Optionnel: rattachement à une compétition

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;           // ACTIF, RESOLU

    @Column(nullable = false)
    private String reportedBy;               // ID du createur de l'incident

    private LocalDateTime reportedAt;        // Date de déclaration
    private LocalDateTime updatedAt;         // Date de modification
    private LocalDateTime resolvedAt;        // Date de résolution

    public Incident() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImpactLevel getImpactLevel() {
        return impactLevel;
    }

    public void setImpactLevel(ImpactLevel impactLevel) {
        this.impactLevel = impactLevel;
    }

    public IncidentType getType() {
        return type;
    }

    public void setType(IncidentType type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
