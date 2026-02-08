package com.ciblorgasport.eventservice.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.ciblorgasport.eventservice.dto.EpreuveDTO;
import com.ciblorgasport.eventservice.model.enums.TypeEpreuve;

@Component
public class EpreuveValidator {

    public void validate(EpreuveDTO dto) {
        if (dto == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Epreuve payload is required");
        if (dto.getTypeEpreuve() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "typeEpreuve is required");
        if (dto.getGenreEpreuve() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "genreEpreuve is required");
        if (dto.getNiveauEpreuve() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "niveauEpreuve is required");

        // time validation
        if (dto.getHeureDebut() != null && dto.getHeureFin() != null && dto.getHeureFin().isBefore(dto.getHeureDebut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "heureFin must be after heureDebut");
        }

        // equipeId must be positive if provided
        if (dto.getEquipeId() != null && dto.getEquipeId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "equipeId must be a positive id");
        }

        // athleteIds must be positive if provided
        if (dto.getAthleteIds() != null) {
            for (Long aid : dto.getAthleteIds()) {
                if (aid == null || aid <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "athleteIds must contain positive ids");
                }
            }
        }

        // do not allow both equipeId and athleteIds at the same time
        if (dto.getEquipeId() != null && dto.getAthleteIds() != null && !dto.getAthleteIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide either equipeId or athleteIds, not both");
        }

        // rules per type
        if (dto.getTypeEpreuve() == TypeEpreuve.INDIVIDUELLE) {
            if (dto.getAthleteIds() == null || dto.getAthleteIds().size() != 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INDIVIDUELLE epreuve must have exactly one athlete id");
            }
            if (dto.getEquipeId() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INDIVIDUELLE epreuve must not have equipeId");
            }
        } else { // COLLECTIVE or others
            if (dto.getEquipeId() == null && (dto.getAthleteIds() == null || dto.getAthleteIds().isEmpty())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "COLLECTIVE epreuve must have a equipeId or at least one athlete id");
            }
        }
    }
}
