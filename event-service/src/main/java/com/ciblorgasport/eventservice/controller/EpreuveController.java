package com.ciblorgasport.eventservice.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Optional;

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

import com.ciblorgasport.eventservice.dto.EpreuveDTO;
import com.ciblorgasport.eventservice.dto.EpreuveMapper;
import com.ciblorgasport.eventservice.validator.EpreuveValidator;

@RestController
@RequestMapping({"/epreuves", "/api/epreuves"})
@PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
public class EpreuveController {
    @Autowired
    private EpreuveRepository epreuveRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private EpreuveMapper epreuveMapper;

    @Autowired
    private EpreuveValidator epreuveValidator;

    @GetMapping
    public List<EpreuveDTO> getAllEpreuves() {
        return epreuveRepository.findAll().stream()
            .map(epreuveMapper::toDto)
            .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<EpreuveDTO> createEpreuve(@Valid @RequestBody EpreuveDTO epreuveDto) {
        epreuveValidator.validate(epreuveDto);
        Epreuve entity = epreuveMapper.toEntity(epreuveDto);
        if (epreuveDto.getCompetitionId() != null) {
            Competition comp = competitionRepository.findById(epreuveDto.getCompetitionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Competition not found with id " + epreuveDto.getCompetitionId()));
            entity.setCompetition(comp);
        }
        Epreuve saved = epreuveRepository.save(entity);
        return new ResponseEntity<>(epreuveMapper.toDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpreuveDTO> getEpreuveById(@PathVariable Long id) {
        return epreuveRepository.findById(id)
            .map(epreuveMapper::toDto)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EpreuveDTO> updateEpreuve(@PathVariable Long id, @Valid @RequestBody EpreuveDTO epreuveDetails) {
        epreuveValidator.validate(epreuveDetails);
        return epreuveRepository.findById(id)
            .map(existing -> {
                epreuveMapper.updateEntityFromDto(existing, epreuveDetails);
                if (epreuveDetails.getCompetitionId() != null) {
                    Competition comp = competitionRepository.findById(epreuveDetails.getCompetitionId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Competition not found with id " + epreuveDetails.getCompetitionId()));
                    existing.setCompetition(comp);
                }
                Epreuve updated = epreuveRepository.save(existing);
                return ResponseEntity.ok(epreuveMapper.toDto(updated));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEpreuve(@PathVariable Long id) {
        if (!epreuveRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        epreuveRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/epreuves/{id}/athletes")
    public ResponseEntity<EpreuveDTO> addAthlete(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        Long athleteId = payload == null ? null : payload.get("athleteId");
        if (athleteId == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Epreuve> opt = epreuveRepository.findById(id);
        if (!opt.isPresent()) return ResponseEntity.notFound().build();

        Epreuve e = opt.get();
        if (e.getAthleteIds() == null) e.setAthleteIds(new HashSet<>());
        e.getAthleteIds().add(athleteId);

        Epreuve saved = epreuveRepository.save(e);
        return ResponseEntity.ok(epreuveMapper.toDto(saved));
    }

    @PostMapping("/epreuves/{id}/athletes/bulk")
    public ResponseEntity<EpreuveDTO> addAthletes(@PathVariable Long id, @RequestBody Map<String, List<Long>> payload) {
        List<Long> athleteIds = payload == null ? null : payload.get("athleteIds");
        if (athleteIds == null || athleteIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Epreuve> opt = epreuveRepository.findById(id);
        if (!opt.isPresent()) return ResponseEntity.notFound().build();

        Epreuve e = opt.get();
        if (e.getAthleteIds() == null) e.setAthleteIds(new HashSet<>());
        e.getAthleteIds().addAll(athleteIds);

        Epreuve saved = epreuveRepository.save(e);
        return ResponseEntity.ok(epreuveMapper.toDto(saved));
    }

    @GetMapping("/epreuves/{id}/athletes")
    public ResponseEntity<Set<Long>> getAthletes(@PathVariable Long id) {
        Optional<Epreuve> opt = epreuveRepository.findById(id);
        if (!opt.isPresent()) return ResponseEntity.notFound().build();
        Epreuve e = opt.get();
        return ResponseEntity.ok(e.getAthleteIds() == null ? Collections.emptySet() : e.getAthleteIds());
    }
}
