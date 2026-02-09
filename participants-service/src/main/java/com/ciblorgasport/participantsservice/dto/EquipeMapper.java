package com.ciblorgasport.participantsservice.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ciblorgasport.participantsservice.model.Equipe;

/**
 * Mapper simple pour Equipe.
 */
@Component
public class EquipeMapper {

    public EquipeDto toDto(Equipe equipe) {
        if (equipe == null) return null;
        EquipeDto dto = new EquipeDto();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setPays(equipe.getPays());
        if (equipe.getAthletes() != null) {
            List<Long> ids = equipe.getAthletes().stream()
                    .map(athlete -> athlete.getId())
                    .collect(Collectors.toList());
            dto.setAthleteIds(ids);
        }
        return dto;
    }
}
