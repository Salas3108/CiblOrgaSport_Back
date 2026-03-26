package com.ciblorgasport.participantsservice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.participantsservice.dto.AthleteDto;
import com.ciblorgasport.participantsservice.dto.AthleteMapper;
import com.ciblorgasport.participantsservice.model.Equipe;
import com.ciblorgasport.participantsservice.service.EquipeService;

/**
 * Endpoint interne : expose la composition d'une équipe (avec sexe des athlètes)
 * pour la validation d'éligibilité inter-service (event-service).
 */
@RestController
@RequestMapping("/internal/equipes")
public class InternalEquipeController {

    private final EquipeService equipeService;
    private final AthleteMapper athleteMapper;

    public InternalEquipeController(EquipeService equipeService, AthleteMapper athleteMapper) {
        this.equipeService = equipeService;
        this.athleteMapper = athleteMapper;
    }

    @GetMapping("/{id}/athletes")
    public ResponseEntity<List<AthleteDto>> getAthletes(@PathVariable Long id) {
        Equipe equipe = equipeService.findByIdOrThrow(id);
        List<AthleteDto> athletes = equipe.getAthletes().stream()
                .map(athleteMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(athletes);
    }
}
