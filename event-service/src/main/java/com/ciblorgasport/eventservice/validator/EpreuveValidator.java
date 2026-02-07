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

        // participant ids must be positive
        if (dto.getParticipantIds() != null) {
            for (Long pid : dto.getParticipantIds()) {
                if (pid == null || pid <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "participantIds must contain positive ids");
                }
            }
        }

        if (dto.getTypeEpreuve() == TypeEpreuve.INDIVIDUELLE) {
            if (dto.getParticipantIds() == null || dto.getParticipantIds().size() != 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INDIVIDUELLE epreuve must have exactly one participant id");
            }
        } else { // COLLECTIVE
            if (dto.getParticipantIds() == null || dto.getParticipantIds().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "COLLECTIVE epreuve must have at least one participant id (athlete or team)");
            }
        }
    }
}
