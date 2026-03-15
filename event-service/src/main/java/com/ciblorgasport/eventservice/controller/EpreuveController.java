package com.ciblorgasport.eventservice.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import com.ciblorgasport.eventservice.model.Epreuve;
import com.ciblorgasport.eventservice.model.Competition;
import com.ciblorgasport.eventservice.repository.EpreuveRepository;
import com.ciblorgasport.eventservice.repository.CompetitionRepository;

import com.ciblorgasport.eventservice.client.LieuServiceClient;
import com.ciblorgasport.eventservice.client.ParticipantsServiceClient;
import com.ciblorgasport.eventservice.dto.EpreuveDTO;
import com.ciblorgasport.eventservice.dto.EpreuveMapper;
import com.ciblorgasport.eventservice.validator.EpreuveValidator;
import com.ciblorgasport.eventservice.model.enums.TypeEpreuve;

@RestController
@RequestMapping({"/epreuves", "/api/epreuves"})
public class EpreuveController {
    @Autowired
    private EpreuveRepository epreuveRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private EpreuveMapper epreuveMapper;

    @Autowired
    private EpreuveValidator epreuveValidator;

    @Autowired
    private LieuServiceClient lieuServiceClient;

    @Autowired
    private ParticipantsServiceClient participantsServiceClient;

    @GetMapping
    public List<EpreuveDTO> getAllEpreuves() {
        return epreuveRepository.findAll().stream()
            .map(epreuveMapper::toDto)
            .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<EpreuveDTO> createEpreuve(@Valid @RequestBody EpreuveDTO epreuveDto) {
        epreuveValidator.validateForCreate(epreuveDto);
        Epreuve entity = epreuveMapper.toEntity(epreuveDto);
        if (epreuveDto.getCompetitionId() != null) {
            Competition comp = competitionRepository.findById(epreuveDto.getCompetitionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Competition not found with id " + epreuveDto.getCompetitionId()));
            entity.setCompetition(comp);
        }
        validateLieuExists(epreuveDto.getLieuId());
        validateAthletesExist(epreuveDto.getAthleteIds());
        validateEquipesExist(epreuveDto.getEquipeIds());
        Epreuve saved = epreuveRepository.save(entity);
        return new ResponseEntity<>(epreuveMapper.toDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpreuveDTO> getEpreuveById(@PathVariable Long id) {
        Epreuve e = epreuveRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Epreuve not found with id " + id));
        return ResponseEntity.ok(epreuveMapper.toDto(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<EpreuveDTO> updateEpreuve(@PathVariable Long id, @Valid @RequestBody EpreuveDTO epreuveDetails) {
        epreuveValidator.validate(epreuveDetails);
        Epreuve existing = epreuveRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Epreuve not found with id " + id));
        epreuveMapper.updateEntityFromDto(existing, epreuveDetails);
        if (epreuveDetails.getCompetitionId() != null) {
            Competition comp = competitionRepository.findById(epreuveDetails.getCompetitionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Competition not found with id " + epreuveDetails.getCompetitionId()));
            existing.setCompetition(comp);
        }
        validateLieuExists(epreuveDetails.getLieuId());
        validateAthletesExist(epreuveDetails.getAthleteIds());
        validateEquipesExist(epreuveDetails.getEquipeIds());
        Epreuve updated = epreuveRepository.save(existing);
        return ResponseEntity.ok(epreuveMapper.toDto(updated));
    }

    private void validateLieuExists(Long lieuId) {
        if (lieuId == null) {
            return;
        }

        try {
            if (!lieuServiceClient.existsById(lieuId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lieu not found with id " + lieuId);
            }
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to validate lieu with lieu-service", ex);
        }
    }

    private void validateAthletesExist(Iterable<Long> athleteIds) {
        if (athleteIds == null) {
            return;
        }

        List<Long> ids = new java.util.ArrayList<>();
        for (Object rawId : (Iterable<?>) athleteIds) {
            if (rawId instanceof Number) {
                ids.add(((Number) rawId).longValue());
            }
        }

        if (ids.isEmpty()) {
            return;
        }

        try {
            if (!participantsServiceClient.areValidAthletes(ids)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more athleteIds are invalid or not validated");
            }
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to validate athleteIds with participants-service", ex);
        }
    }

    private void validateEquipesExist(Collection<Long> equipeIds) {
        if (equipeIds == null || equipeIds.isEmpty()) {
            return;
        }

        try {
            if (!participantsServiceClient.areValidEquipes(equipeIds)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more equipeIds are invalid");
            }
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to validate equipeIds with participants-service", ex);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<Void> deleteEpreuve(@PathVariable Long id) {
        if (!epreuveRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Epreuve not found with id " + id);
        }
        epreuveRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/athletes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<EpreuveDTO> addAthlete(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        Long athleteId = payload == null ? null : payload.get("athleteId");
        if (athleteId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required field 'athleteId'");
        }
        validateAthletesExist(Collections.singletonList(athleteId));
        Epreuve e = epreuveRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Epreuve not found with id " + id));
        if (e.getTypeEpreuve() == TypeEpreuve.COLLECTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "COLLECTIVE epreuve must not have athleteIds");
        }
        if (e.getAthleteIds() == null) e.setAthleteIds(new HashSet<>());
        e.getAthleteIds().add(athleteId);
        Epreuve saved = epreuveRepository.save(e);
        return ResponseEntity.ok(epreuveMapper.toDto(saved));
    }

    @PostMapping({"/{id}/equipe", "/{id}/equipes"})
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<EpreuveDTO> addEquipes(@PathVariable Long id, @RequestBody Map<String, List<?>> payload) {
        List<?> rawEquipeIds = payload == null ? null : payload.get("equipeIds");
        if (rawEquipeIds == null || rawEquipeIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'equipeIds' must be provided and non-empty");
        }
        List<Long> equipeIds = new java.util.ArrayList<>();
        for (Object rawId : rawEquipeIds) {
            Long equipeId;
            if (rawId instanceof Number number) {
                equipeId = number.longValue();
            } else if (rawId instanceof String str && !str.isBlank()) {
                try {
                    equipeId = Long.parseLong(str);
                } catch (NumberFormatException ex) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'equipeIds' must contain positive ids");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'equipeIds' must contain positive ids");
            }

            if (equipeId <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'equipeIds' must contain positive ids");
            }
            equipeIds.add(equipeId);
        }

        Epreuve e = epreuveRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Epreuve not found with id " + id));

        if (e.getTypeEpreuve() == TypeEpreuve.INDIVIDUELLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INDIVIDUELLE epreuve must not have equipeIds");
        }

        if (e.getAthleteIds() != null && !e.getAthleteIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide either equipeIds or athleteIds, not both");
        }

        validateEquipesExist(equipeIds);
        if (e.getEquipeIds() == null) e.setEquipeIds(new HashSet<>());
        e.getEquipeIds().addAll(equipeIds);
        Epreuve saved = epreuveRepository.save(e);
        return ResponseEntity.ok(epreuveMapper.toDto(saved));
    }

    @PostMapping("/{id}/athletes/bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<EpreuveDTO> addAthletes(@PathVariable Long id, @RequestBody Map<String, List<Long>> payload) {
        List<Long> athleteIds = payload == null ? null : payload.get("athleteIds");
        if (athleteIds == null || athleteIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'athleteIds' must be provided and non-empty");
        }
        // Normaliser en Long (Jackson désérialise les petits nombres en Integer)
        List<Long> normalizedIds = new java.util.ArrayList<>();
        for (Object rawId : (List<?>) athleteIds) {
            if (rawId instanceof Number) normalizedIds.add(((Number) rawId).longValue());
        }
        validateAthletesExist(normalizedIds);
        Epreuve e = epreuveRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Epreuve not found with id " + id));
        if (e.getTypeEpreuve() == TypeEpreuve.COLLECTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "COLLECTIVE epreuve must not have athleteIds");
        }
        if (e.getAthleteIds() == null) e.setAthleteIds(new HashSet<>());
        e.getAthleteIds().addAll(normalizedIds);
        Epreuve saved = epreuveRepository.save(e);
        return ResponseEntity.ok(epreuveMapper.toDto(saved));
    }

    @GetMapping("/{id}/athletes")
    public ResponseEntity<Set<Long>> getAthletes(@PathVariable Long id) {
        Epreuve e = epreuveRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Epreuve not found with id " + id));
        return ResponseEntity.ok(e.getAthleteIds() == null ? Collections.emptySet() : e.getAthleteIds());
    }

    @GetMapping("/{id}/equipes")
    public ResponseEntity<Set<Long>> getEquipes(@PathVariable Long id) {
        Epreuve e = epreuveRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Epreuve not found with id " + id));
        return ResponseEntity.ok(e.getEquipeIds() == null ? Collections.emptySet() : e.getEquipeIds());
    }

    @GetMapping("/{id}/athletes/{athleteId}")
    public ResponseEntity<Map<String, Boolean>> isAthleteParticipating(@PathVariable Long id, @PathVariable Long athleteId) {
        Epreuve e = epreuveRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Epreuve not found with id " + id));
        boolean participating = e.getAthleteIds() != null && e.getAthleteIds().contains(athleteId);
        return ResponseEntity.ok(Collections.singletonMap("participating", participating));
    }

    @GetMapping("/athletes/{athleteId}")
    public ResponseEntity<List<EpreuveDTO>> getEpreuvesForAthlete(@PathVariable Long athleteId) {
        if (athleteId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required athleteId");
        }
        List<EpreuveDTO> epreuves = epreuveRepository.findByAthleteIdsContains(athleteId).stream()
            .map(epreuveMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(epreuves);
    }
}