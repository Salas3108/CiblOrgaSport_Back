package com.ciblorgasport.participantsservice.controller;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.participantsservice.dto.AthleteDto;
import com.ciblorgasport.participantsservice.dto.AthleteMapper;
import com.ciblorgasport.participantsservice.dto.MessageDto;
import com.ciblorgasport.participantsservice.dto.MessageMapper;
import com.ciblorgasport.participantsservice.dto.request.CreateMessageRequest;
import com.ciblorgasport.participantsservice.dto.request.ValidationRequest;
import com.ciblorgasport.participantsservice.model.Athlete;
import com.ciblorgasport.participantsservice.model.Message;
import com.ciblorgasport.participantsservice.service.AthleteService;

/**
 * Endpoints côté COMMISSAIRE.
 */
@RestController
@RequestMapping({"/commissaire", "/api/commissaire"})
@PreAuthorize("hasRole('COMMISSAIRE')")
public class CommissaireController {

    private final AthleteService athleteService;
    private final AthleteMapper athleteMapper;
    private final MessageMapper messageMapper;

    public CommissaireController(AthleteService athleteService, AthleteMapper athleteMapper, MessageMapper messageMapper) {
        this.athleteService = athleteService;
        this.athleteMapper = athleteMapper;
        this.messageMapper = messageMapper;
    }

    // COMMISSAIRE : get all athletes
    @GetMapping("/athletes")
    public ResponseEntity<List<AthleteDto>> getAllAthletes() {
        List<AthleteDto> athletes = athleteService.findAll().stream()
                .map(athleteMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(athletes);
    }

    // COMMISSAIRE : get validated athletes
    @GetMapping("/athletes/valides")
    public ResponseEntity<List<AthleteDto>> getValidatedAthletes() {
        List<AthleteDto> athletes = athleteService.findValidated().stream()
                .map(athleteMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(athletes);
    }

    // COMMISSAIRE : get info
    @GetMapping("/athletes/{id}/info")
    public ResponseEntity<AthleteDto> getInfo(@PathVariable Long id) {
        Athlete athlete = athleteService.findByIdOrThrow(id);
        return ResponseEntity.ok(athleteMapper.toDto(athlete));
    }

    // COMMISSAIRE : get doc
    @GetMapping("/athletes/{id}/doc")
    public ResponseEntity<?> getDoc(@PathVariable Long id) {
        Athlete athlete = athleteService.findByIdOrThrow(id);
        // On renvoie la structure docs directement (comme dans le mock)
        return ResponseEntity.ok(Map.of("docs", athleteMapper.toDto(athlete).getDocs()));
    }

    // COMMISSAIRE : post validation -> true/false (+ motifRefus)
    @PostMapping("/athletes/{id}/validation")
    public ResponseEntity<AthleteDto> postValidation(@PathVariable Long id, @RequestBody ValidationRequest request) {
        return ResponseEntity.ok(athleteMapper.toDto(athleteService.validate(id, request)));
    }

    // COMMISSAIRE : post message (ex: "passport expiré")
    @PostMapping("/athletes/{id}/message")
    public ResponseEntity<MessageDto> postMessage(@PathVariable Long id, @RequestBody CreateMessageRequest request) {
        Message message = athleteService.createMessage(id, request.getContenu());
        return ResponseEntity.ok(messageMapper.toDto(message));
    }
}
