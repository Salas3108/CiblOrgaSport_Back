package com.ciblorgasport.participantsservice.dto;

import java.util.Map;
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
            // Crée un map ID -> username
            Map<Long, String> idUsernameMap = equipe.getAthletes().stream()
                    .collect(Collectors.toMap(
                            athlete -> athlete.getId(),
                            athlete -> athlete.getUsername() != null ? athlete.getUsername() : ""
                    ));
            dto.setAthleteIdUsernameMap(idUsernameMap);
        }

        return dto;
    }
}