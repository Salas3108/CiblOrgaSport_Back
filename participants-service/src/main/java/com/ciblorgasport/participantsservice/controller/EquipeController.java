package com.ciblorgasport.participantsservice.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.participantsservice.dto.EquipeDto;
import com.ciblorgasport.participantsservice.dto.EquipeMapper;
import com.ciblorgasport.participantsservice.dto.request.AssignAthletesRequest;
import com.ciblorgasport.participantsservice.dto.request.CreateEquipeRequest;
import com.ciblorgasport.participantsservice.dto.request.UpdateEquipeRequest;
import com.ciblorgasport.participantsservice.service.EquipeService;

/**
 * Endpoints cote COMMISSAIRE pour la gestion des equipes.
 */
@RestController
@RequestMapping({"/commissaire/equipes", "/api/commissaire/equipes"})
@PreAuthorize("hasRole('COMMISSAIRE') or hasRole('ADMIN')")
public class EquipeController {

    private final EquipeService equipeService;
    private final EquipeMapper equipeMapper;

    public EquipeController(EquipeService equipeService, EquipeMapper equipeMapper) {
        this.equipeService = equipeService;
        this.equipeMapper = equipeMapper;
    }

    /** Returns all equipes. */
    @GetMapping("")
    public ResponseEntity<List<EquipeDto>> getAll() {
        List<EquipeDto> equipes = equipeService.findAll().stream()
                .map(equipeMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(equipes);
    }

    /** Returns an equipe by its ID. */
    @GetMapping("/{id}")
    public ResponseEntity<EquipeDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(equipeMapper.toDto(equipeService.findByIdOrThrow(id)));
    }

    /** Creates a new equipe. */
    @PostMapping("")
    public ResponseEntity<EquipeDto> create(@RequestBody CreateEquipeRequest request) {
        return ResponseEntity.ok(equipeMapper.toDto(equipeService.create(request)));
    }

    /** Updates an existing equipe by its ID. */
    @PutMapping("/{id}")
    public ResponseEntity<EquipeDto> update(@PathVariable Long id, @RequestBody UpdateEquipeRequest request) {
        return ResponseEntity.ok(equipeMapper.toDto(equipeService.update(id, request)));
    }

    /** Deletes an equipe by its ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        equipeService.delete(id);
        return ResponseEntity.ok(Map.of("message", "equipe supprime avec succes"));
    }

    /** Assigns athletes to an equipe. */
    @PostMapping("/{id}/athletes")
    public ResponseEntity<EquipeDto> assignAthletes(@PathVariable Long id, @RequestBody AssignAthletesRequest request) {
        return ResponseEntity.ok(equipeMapper.toDto(equipeService.assignAthletes(id, request)));
    }
}
