package com.ciblorgasport.resultatsservice.dto;

import org.springframework.stereotype.Component;

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
}
