package com.ciblorgasport.participantsservice.controller;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.ciblorgasport.participantsservice.dto.AthleteDto;
import com.ciblorgasport.participantsservice.dto.AthleteMapper;
import com.ciblorgasport.participantsservice.dto.AuthAthleteSummary;
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
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${auth-service.base-url:http://localhost:8081}")
    private String authServiceBaseUrl;

    public CommissaireController(AthleteService athleteService, AthleteMapper athleteMapper, MessageMapper messageMapper) {
        this.athleteService = athleteService;
        this.athleteMapper = athleteMapper;
        this.messageMapper = messageMapper;
    }

    // COMMISSAIRE : get all athletes
    @GetMapping("/athletes")
    public ResponseEntity<List<AthleteDto>> getAllAthletes() {
        List<Athlete> athletes = athleteService.findAll();
        for (Athlete athlete : athletes) {
            if (athlete.getUsername() == null || athlete.getUsername().isBlank()) {
                String username = fetchUsernameFromAuth(athlete.getId());
                if (username != null && !username.isBlank()) {
                    athleteService.updateUsernameIfMissing(athlete.getId(), username);
                }
            }
        }
        List<AthleteDto> dtos = athleteService.findAll().stream()
                .map(athleteMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // COMMISSAIRE : sync athletes from auth-service (for older accounts)
    @PostMapping("/athletes/sync")
    public ResponseEntity<Map<String, Object>> syncAthletes() {
        SyncStats stats = syncAthletesFromAuth();
        return ResponseEntity.ok(Map.of("total", stats.total, "created", stats.created));
    }

    private SyncStats syncAthletesFromAuth() {
        String url = authServiceBaseUrl + "/auth/internal/athletes";
        AuthAthleteSummary[] authAthletes = restTemplate.getForObject(url, AuthAthleteSummary[].class);
        if (authAthletes == null) {
            return new SyncStats(0, 0);
        }
        int total = 0;
        int created = 0;
        for (AuthAthleteSummary authAthlete : Arrays.asList(authAthletes)) {
            if (authAthlete == null || authAthlete.getId() == null) continue;
            total++;
            boolean existed = athleteService.existsById(authAthlete.getId());
            athleteService.createIfMissingForUser(authAthlete.getId(), authAthlete.getUsername());
            if (!existed) created++;
        }
        return new SyncStats(total, created);
    }

    private String fetchUsernameFromAuth(Long athleteId) {
        if (athleteId == null) return null;
        try {
            String url = authServiceBaseUrl + "/auth/user/" + athleteId;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Object username = response != null ? response.get("username") : null;
            return Objects.toString(username, null);
        } catch (Exception ex) {
            return null;
        }
    }

    private static class SyncStats {
        private final int total;
        private final int created;

        private SyncStats(int total, int created) {
            this.total = total;
            this.created = created;
        }
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
