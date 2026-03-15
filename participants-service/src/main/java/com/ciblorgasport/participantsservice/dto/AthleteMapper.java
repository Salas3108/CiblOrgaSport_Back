package com.ciblorgasport.participantsservice.dto;

import org.springframework.stereotype.Component;

import com.ciblorgasport.participantsservice.model.Athlete;
import com.ciblorgasport.participantsservice.model.AthleteDocs;

/**
 * Mapper simple (sans MapStruct) pour rester cohérent avec le projet et garder le contrôle.
 */
@Component
public class AthleteMapper {

    public AthleteDto toDto(Athlete athlete) {
        if (athlete == null) return null;
        AthleteDto dto = new AthleteDto();
        dto.setId(athlete.getId());
        dto.setUsername(athlete.getUsername());
        dto.setNom(athlete.getNom());
        dto.setPrenom(athlete.getPrenom());
        dto.setDateNaissance(athlete.getDateNaissance());
        dto.setPays(athlete.getPays());
        dto.setValide(athlete.isValide());
        dto.setObservation(athlete.getObservation());
        dto.setMotifRefus(athlete.getMotifRefus());
        if (athlete.getEquipe() != null) {
            dto.setEquipeId(athlete.getEquipe().getId());
            dto.setEquipeNom(athlete.getEquipe().getNom());
        } else {
            dto.setEquipeId(null);
            dto.setEquipeNom(null);
        }

        if (athlete.getDocs() != null) {
            dto.setDocs(new AthleteDocsDto(athlete.getDocs().getCertificatMedical(), athlete.getDocs().getPassport()));
        } else {
            dto.setDocs(new AthleteDocsDto(null, null));
        }

        return dto;
    }

    public AthleteDocs toEntity(AthleteDocsDto dto) {
        if (dto == null) return null;
        return new AthleteDocs(dto.getCertificatMedical(), dto.getPassport());
    }
}
