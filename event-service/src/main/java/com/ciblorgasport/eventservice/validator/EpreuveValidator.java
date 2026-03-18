package com.ciblorgasport.eventservice.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.ciblorgasport.eventservice.dto.EpreuveDTO;
import com.ciblorgasport.eventservice.model.enums.TypeEpreuve;

@Component
public class EpreuveValidator {

    public void validate(EpreuveDTO dto) {
        validateCommon(dto);

        // strict rules (kept for update/full payload validation)
        if (dto.getTypeEpreuve() == TypeEpreuve.INDIVIDUELLE) {
            if (dto.getAthleteIds() == null || dto.getAthleteIds().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INDIVIDUELLE epreuve must have at least one athlete id");
            }
            if (dto.getEquipeIds() != null && !dto.getEquipeIds().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INDIVIDUELLE epreuve must not have equipeIds");
            }
        } else { // COLLECTIVE or others
            if (dto.getEquipeIds() == null || dto.getEquipeIds().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "COLLECTIVE epreuve must have at least one equipe id");
            }
            if (dto.getAthleteIds() != null && !dto.getAthleteIds().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "COLLECTIVE epreuve must not have athleteIds");
            }
        }
    }

    public void validateForCreate(EpreuveDTO dto) {
        validateCommon(dto);

        // relaxed rules for creation: participants/team can be added later via dedicated endpoints
        if (dto.getTypeEpreuve() == TypeEpreuve.INDIVIDUELLE && dto.getEquipeIds() != null && !dto.getEquipeIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INDIVIDUELLE epreuve must not have equipeIds");
        }
        if (dto.getTypeEpreuve() == TypeEpreuve.COLLECTIVE && dto.getAthleteIds() != null && !dto.getAthleteIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "COLLECTIVE epreuve must not have athleteIds");
        }
    }

    private void validateCommon(EpreuveDTO dto) {
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

        // equipeIds must be positive if provided
        if (dto.getEquipeIds() != null) {
            for (Long eid : dto.getEquipeIds()) {
                if (eid == null || eid <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "equipeIds must contain positive ids");
                }
            }
        }

        // athleteIds must be positive if provided
        if (dto.getAthleteIds() != null) {
            for (Long aid : dto.getAthleteIds()) {
                if (aid == null || aid <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "athleteIds must contain positive ids");
                }
            }
        }

        // do not allow both equipeIds and athleteIds at the same time
        if (dto.getEquipeIds() != null && !dto.getEquipeIds().isEmpty()
                && dto.getAthleteIds() != null && !dto.getAthleteIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide either equipeIds or athleteIds, not both");
        }
    }
}
