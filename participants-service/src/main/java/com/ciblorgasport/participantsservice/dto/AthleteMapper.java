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
        if (athlete == null || athlete.getId() == null) {
            return toDtoWithDownloadUrls(athlete, null);
        }
        return toDtoWithDownloadUrls(athlete, String.valueOf(athlete.getId()));
    }

    public AthleteDto toDtoWithDownloadUrls(Athlete athlete, String athleteId) {
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
            AthleteDocs docs = athlete.getDocs();
            String certUrl = null;
            String passportUrl = null;
            
            if (athleteId != null) {
                if (docs.getCertificatMedical() != null && docs.getCertificatMedical().length > 0) {
                    certUrl = "/athlete/" + athleteId + "/doc/certificatMedical";
                }
                if (docs.getPassport() != null && docs.getPassport().length > 0) {
                    passportUrl = "/athlete/" + athleteId + "/doc/passport";
                }
            }
            
            dto.setDocs(new AthleteDocsDto(certUrl, passportUrl));
        } else {
            dto.setDocs(new AthleteDocsDto(null, null));
        }

        return dto;
    }

    public AthleteDocs toEntity(AthleteDocsDto dto) {
        if (dto == null) return null;
        return new AthleteDocs(null, null);
    }
}
