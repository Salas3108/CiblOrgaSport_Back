package com.ciblorgasport.resultatsservice.dto;

import org.springframework.stereotype.Component;

import com.ciblorgasport.resultatsservice.client.dto.AthleteInfoDto;
import com.ciblorgasport.resultatsservice.client.dto.EpreuveContextDto;
import com.ciblorgasport.resultatsservice.client.dto.EquipeInfoDto;
import com.ciblorgasport.resultatsservice.model.Resultat;

@Component
public class ResultatMapper {

    public ResultatDto toDto(Resultat resultat) {
        if (resultat == null) return null;
        ResultatDto dto = new ResultatDto();
        dto.setId(resultat.getId());
        dto.setClassement(resultat.getClassement());
        dto.setMedaille(resultat.getMedaille());
        dto.setQualification(resultat.isQualification());
        dto.setValeurPrincipale(resultat.getValeurPrincipale());
        dto.setUnite(resultat.getUnite());
        dto.setDetailsPerformance(resultat.getDetailsPerformance());
        dto.setTypePerformance(resultat.getTypePerformance());
        dto.setAthleteId(resultat.getAthleteId());
        dto.setEquipeId(resultat.getEquipeId());
        dto.setEpreuveId(resultat.getEpreuveId());
        dto.setStatut(resultat.getStatut());
        dto.setPublished(resultat.isPublished());
        return dto;
    }

    public ResultatDto toDtoEnrichi(Resultat resultat, AthleteInfoDto athlete, EquipeInfoDto equipe, EpreuveContextDto ctx) {
        ResultatDto dto = toDto(resultat);
        if (dto == null) return null;
        if (athlete != null) {
            dto.setAthleteNom(athlete.getNom());
            dto.setAthletePrenom(athlete.getPrenom());
            dto.setAthletePays(athlete.getPays());
        }
        if (equipe != null) {
            dto.setEquipeNom(equipe.getNom());
            dto.setEquipePays(equipe.getPays());
        }
        if (ctx != null) {
            dto.setEpreuveNom(ctx.getNom());
            dto.setDiscipline(ctx.getDiscipline());
            dto.setNiveauEpreuve(ctx.getNiveauEpreuve());
        }
        return dto;
    }
}
