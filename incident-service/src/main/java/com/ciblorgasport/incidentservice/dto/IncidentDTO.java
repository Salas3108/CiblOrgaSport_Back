package com.ciblorgasport.incidentservice.dto;

import java.time.LocalDateTime;

import com.ciblorgasport.incidentservice.model.ImpactLevel;
import com.ciblorgasport.incidentservice.model.IncidentStatus;
import com.ciblorgasport.incidentservice.model.IncidentType;

public class IncidentDTO {
    private Long id;
    private String description;
    private ImpactLevel impactLevel;
    private IncidentType type;
    private String location;
    private IncidentStatus status;
    private String reportedBy;
    private LocalDateTime reportedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ImpactLevel getImpactLevel() { return impactLevel; }
    public void setImpactLevel(ImpactLevel impactLevel) { this.impactLevel = impactLevel; }

    public IncidentType getType() { return type; }
    public void setType(IncidentType type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }

    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
