package com.ciblorgasport.participantsservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.participantsservice.dto.AthleteDto;
import com.ciblorgasport.participantsservice.dto.AthleteMapper;
import com.ciblorgasport.participantsservice.dto.request.InternalAthleteCreateRequest;
import com.ciblorgasport.participantsservice.service.AthleteService;

/**
 * Endpoint interne pour la creation d'athletes lors de l'inscription.
 */
@RestController
@RequestMapping("/internal/athletes")
public class InternalAthleteController {

    private final AthleteService athleteService;
    private final AthleteMapper athleteMapper;

    public InternalAthleteController(AthleteService athleteService, AthleteMapper athleteMapper) {
        this.athleteService = athleteService;
        this.athleteMapper = athleteMapper;
    }

    @PostMapping("")
    public ResponseEntity<AthleteDto> create(@RequestBody InternalAthleteCreateRequest request) {
        if (request == null || request.getId() == null || request.getUsername() == null || request.getUsername().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        var athlete = athleteService.createIfMissingForUser(request.getId(), request.getUsername());
        return ResponseEntity.ok(athleteMapper.toDto(athlete));
    }
}
