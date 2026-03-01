package com.ciblorgasport.eventservice.dto;

import org.springframework.stereotype.Component;
import com.ciblorgasport.eventservice.model.Epreuve;
import com.ciblorgasport.eventservice.model.enums.StatutEpreuve;
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
        dto.setDateHeure(e.getDateHeure());
        dto.setDureeMinutes(e.getDureeMinutes());
        dto.setStatut(e.getStatut());
        dto.setTypeEpreuve(e.getTypeEpreuve());
        dto.setGenreEpreuve(e.getGenreEpreuve());
        dto.setNiveauEpreuve(e.getNiveauEpreuve());
        dto.setEquipeId(e.getEquipeId());
        if (e.getCompetition() != null) dto.setCompetitionId(e.getCompetition().getId());
        if (e.getLieu() != null) dto.setLieuId(e.getLieu().getId());
        dto.setAthleteIds(e.getAthleteIds());
        return dto;
    }

    public Epreuve toEntity(EpreuveDTO dto) {
        if (dto == null) return null;
        Epreuve e = new Epreuve();
        e.setId(dto.getId());
        e.setNom(dto.getNom());
        e.setDescription(dto.getDescription());
        e.setDateHeure(dto.getDateHeure());
        e.setDureeMinutes(dto.getDureeMinutes());
        e.setStatut(dto.getStatut() != null ? dto.getStatut() : StatutEpreuve.PLANIFIE);
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
        entity.setDateHeure(dto.getDateHeure());
        entity.setDureeMinutes(dto.getDureeMinutes());
        entity.setStatut(dto.getStatut() != null ? dto.getStatut() : StatutEpreuve.PLANIFIE);
        entity.setTypeEpreuve(dto.getTypeEpreuve());
        entity.setGenreEpreuve(dto.getGenreEpreuve());
        entity.setNiveauEpreuve(dto.getNiveauEpreuve());
        if (dto.getEquipeId() != null) {
            entity.setEquipeId(dto.getEquipeId());
        }
    }
}
