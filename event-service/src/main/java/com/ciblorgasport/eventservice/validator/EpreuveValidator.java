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
        if (dto.getDateHeure() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateHeure is required");
        if (dto.getDureeMinutes() == null || dto.getDureeMinutes() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dureeMinutes must be a positive value");
        }

        // lieuId must be positive if provided
        if (dto.getLieuId() != null && dto.getLieuId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lieuId must be a positive id");
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
            if (dto.getAthleteIds() == null || dto.getAthleteIds().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INDIVIDUELLE epreuve must have at least one athlete id");
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
