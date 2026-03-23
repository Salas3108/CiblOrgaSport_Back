package com.ciblorgasport.incidentservice.dto;

import org.springframework.stereotype.Component;

import com.ciblorgasport.incidentservice.model.Incident;

@Component
public class IncidentMapper {

    public IncidentDTO toDto(Incident i) {
        if (i == null) return null;
        IncidentDTO dto = new IncidentDTO();
        dto.setId(i.getId());
        dto.setDescription(i.getDescription());
        dto.setImpactLevel(i.getImpactLevel());
        dto.setType(i.getType());
        dto.setLieuId(i.getLieuId());
        dto.setCompetitionId(i.getCompetitionId());
        dto.setStatus(i.getStatus());
        dto.setReportedBy(i.getReportedBy());
        dto.setReportedAt(i.getReportedAt());
        dto.setUpdatedAt(i.getUpdatedAt());
        dto.setResolvedAt(i.getResolvedAt());
        return dto;
    }

    public Incident toEntity(IncidentDTO dto) {
        if (dto == null) return null;
        Incident i = new Incident();
        i.setId(dto.getId());
        i.setDescription(dto.getDescription());
        i.setImpactLevel(dto.getImpactLevel());
        i.setType(dto.getType());
        i.setLieuId(dto.getLieuId());
        i.setCompetitionId(dto.getCompetitionId());
        i.setStatus(dto.getStatus());
        i.setReportedBy(dto.getReportedBy());
        i.setReportedAt(dto.getReportedAt());
        i.setUpdatedAt(dto.getUpdatedAt());
        i.setResolvedAt(dto.getResolvedAt());
        return i;
    }

}
