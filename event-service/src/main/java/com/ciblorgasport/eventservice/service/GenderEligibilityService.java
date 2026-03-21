package com.ciblorgasport.eventservice.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ciblorgasport.eventservice.client.ParticipantsServiceClient;
import com.ciblorgasport.eventservice.dto.AthleteSexeDto;
import com.ciblorgasport.eventservice.model.enums.GenreEpreuve;

/**
 * Vérifie la compatibilité de genre entre un athlète (ou une équipe) et une épreuve.
 *
 * Règle individuelle :
 *   - L'athlète doit être validé (valide=true).
 *   - Si genreEpreuve == FEMININ, l'athlète doit avoir sexe == "FEMININ".
 *   - Si genreEpreuve == MASCULIN, l'athlète doit avoir sexe == "MASCULIN".
 *   - Si genreEpreuve == MIXTE, toujours accepté.
 *
 * Règle collective :
 *   - Le genre agrégé de l'équipe est calculé à partir des athlètes membres.
 *   - Tous FEMININ → équipe FEMININE ; tous MASCULIN → équipe MASCULINE ; sinon MIXTE.
 *   - Une équipe mixte ne peut pas participer à une épreuve à genre unique (FEMININ ou MASCULIN).
 */
@Service
public class GenderEligibilityService {

    private final ParticipantsServiceClient participantsServiceClient;

    public GenderEligibilityService(ParticipantsServiceClient participantsServiceClient) {
        this.participantsServiceClient = participantsServiceClient;
    }

    public void validateAthlete(Long athleteId, GenreEpreuve genreEpreuve) {
        if (genreEpreuve == GenreEpreuve.MIXTE) {
            return;
        }

        AthleteSexeDto athlete = participantsServiceClient.getAthleteInfo(athleteId);
        if (athlete == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Athlète introuvable dans participants-service : " + athleteId);
        }
        if (!athlete.isValide()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "L'athlète " + athleteId + " n'est pas encore validé par un commissaire");
        }
        if (athlete.getSexe() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "L'athlète " + athleteId + " n'a pas de genre renseigné dans son profil");
        }

        boolean compatible = genreEpreuve.name().equals(athlete.getSexe());
        if (!compatible) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "L'athlète " + athleteId + " (sexe: " + athlete.getSexe()
                    + ") n'est pas éligible à l'épreuve " + genreEpreuve.name());
        }
    }

    public void validateEquipe(Long equipeId, GenreEpreuve genreEpreuve) {
        if (genreEpreuve == GenreEpreuve.MIXTE) {
            return;
        }

        List<AthleteSexeDto> athletes = participantsServiceClient.getEquipeAthletes(equipeId);
        if (athletes.isEmpty()) {
            return;
        }

        GenreEpreuve equipeGenre = computeEquipeGenre(athletes);

        if (equipeGenre == GenreEpreuve.MIXTE) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "L'équipe " + equipeId + " a une composition mixte et ne peut pas participer "
                    + "à l'épreuve " + genreEpreuve.name());
        }
        if (equipeGenre != genreEpreuve) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "L'équipe " + equipeId + " (genre: " + equipeGenre.name()
                    + ") n'est pas éligible à l'épreuve " + genreEpreuve.name());
        }
    }

    private GenreEpreuve computeEquipeGenre(List<AthleteSexeDto> athletes) {
        boolean hasFeminin = false;
        boolean hasMasculin = false;
        for (AthleteSexeDto a : athletes) {
            if ("FEMININ".equals(a.getSexe())) {
                hasFeminin = true;
            } else if ("MASCULIN".equals(a.getSexe())) {
                hasMasculin = true;
            }
        }
        if (hasFeminin && hasMasculin) return GenreEpreuve.MIXTE;
        if (hasFeminin) return GenreEpreuve.FEMININ;
        if (hasMasculin) return GenreEpreuve.MASCULIN;
        return GenreEpreuve.MIXTE;
    }
}
