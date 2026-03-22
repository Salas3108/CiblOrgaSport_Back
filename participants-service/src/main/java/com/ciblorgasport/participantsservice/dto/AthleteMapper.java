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
        dto.setSexe(athlete.getSexe());
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

            AthleteDocsDto docsDto = new AthleteDocsDto(certUrl, passportUrl);
            docsDto.setDocumentGenre(docs.getDocumentGenre());
            dto.setDocs(docsDto);
        } else {
            dto.setDocs(new AthleteDocsDto(null, null));
        }

        return dto;
    }

    public AthleteDocs toEntity(AthleteDocsDto dto) {
        if (dto == null) return null;
        byte[] certBytes = decodeBase64OrNull(dto.getCertificatMedicalUrl());
        byte[] passportBytes = decodeBase64OrNull(dto.getPassportUrl());
        return new AthleteDocs(certBytes, passportBytes);
    }

    private byte[] decodeBase64OrNull(String base64) {
        if (base64 == null || base64.isBlank()) return null;
        try {
            // Supporte le préfixe "data:application/pdf;base64,..."
            String data = base64.contains(",") ? base64.split(",", 2)[1] : base64;
            return java.util.Base64.getDecoder().decode(data);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
