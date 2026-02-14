package com.ciblorgasport.eventservice.dto;

import org.springframework.stereotype.Component;
import com.ciblorgasport.eventservice.model.Epreuve;
import java.util.HashSet;
import java.util.Set;

@Component
public class EpreuveMapper {

    public EpreuveDTO toDto(Epreuve e) {
        if (e == null) return null;
        EpreuveDTO dto = new EpreuveDTO();
        dto.setId(e.getId());
        dto.setNom(e.getNom());
        dto.setDescription(e.getDescription());
        dto.setDate(e.getDate());
        dto.setHeureDebut(e.getHeureDebut());
        dto.setHeureFin(e.getHeureFin());
        dto.setTypeEpreuve(e.getTypeEpreuve());
        dto.setGenreEpreuve(e.getGenreEpreuve());
        dto.setNiveauEpreuve(e.getNiveauEpreuve());
        dto.setEquipeId(e.getEquipeId());
        if (e.getCompetition() != null) dto.setCompetitionId(e.getCompetition().getId());
        dto.setAthleteIds(e.getAthleteIds());
        return dto;
    }

    public Epreuve toEntity(EpreuveDTO dto) {
        if (dto == null) return null;
        Epreuve e = new Epreuve();
        e.setId(dto.getId());
        e.setNom(dto.getNom());
        e.setDescription(dto.getDescription());
        e.setDate(dto.getDate());
        e.setHeureDebut(dto.getHeureDebut());
        e.setHeureFin(dto.getHeureFin());
        e.setTypeEpreuve(dto.getTypeEpreuve());
        e.setGenreEpreuve(dto.getGenreEpreuve());
        e.setNiveauEpreuve(dto.getNiveauEpreuve());
        e.setEquipeId(dto.getEquipeId());
        e.setAthleteIds(dto.getAthleteIds());
        return e;
    }

    public void updateEntityFromDto(Epreuve entity, EpreuveDTO dto) {
        if (entity == null || dto == null) return;
        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setHeureDebut(dto.getHeureDebut());
        entity.setHeureFin(dto.getHeureFin());
        entity.setTypeEpreuve(dto.getTypeEpreuve());
        entity.setGenreEpreuve(dto.getGenreEpreuve());
        entity.setNiveauEpreuve(dto.getNiveauEpreuve());
        if (dto.getEquipeId() != null) {
            entity.setEquipeId(dto.getEquipeId());
        }
    }
}
