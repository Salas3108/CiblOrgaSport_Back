package com.ciblorgasport.participantsservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ciblorgasport.participantsservice.dto.AthleteMapper;
import com.ciblorgasport.participantsservice.dto.EquipeDto;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteDocsRequest;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteInfoRequest;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteObservationRequest;
import com.ciblorgasport.participantsservice.service.AthleteService;
import com.ciblorgasport.participantsservice.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Endpoints côté ATHLETE.
 *
 * NOTE: on utilise des POST comme demandé.
 */
@RestController
@RequestMapping({"/athlete", "/api/athlete"})
@PreAuthorize("hasRole('ATHLETE')")
public class AthleteController {

    private final AthleteService athleteService;
    private final AthleteMapper athleteMapper;
    private final JwtUtils jwtUtils;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${auth-service.base-url:http://localhost:8081}")
    private String authServiceBaseUrl;

    public AthleteController(AthleteService athleteService, AthleteMapper athleteMapper, JwtUtils jwtUtils) {
        this.athleteService = athleteService;
        this.athleteMapper = athleteMapper;
        this.jwtUtils = jwtUtils;
    }

    // ATHLETE : post info
    @PostMapping("/{id}/info")
    public ResponseEntity<?> postInfo(@PathVariable Long id, @RequestBody UpdateAthleteInfoRequest request, HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null || !tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden: token user id does not match path id"));
        }
        return ResponseEntity.ok(athleteMapper.toDto(athleteService.updateInfo(id, request)));
    }

    // ATHLETE : post doc
    @PostMapping("/{id}/doc")
    public ResponseEntity<?> postDoc(@PathVariable Long id, @RequestBody UpdateAthleteDocsRequest request, HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null || !tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden: token user id does not match path id"));
        }
        return ResponseEntity.ok(athleteMapper.toDto(athleteService.updateDocs(id, request)));
    }

    // ATHLETE : post remarque (observation)
    @PostMapping("/{id}/remarque")
    public ResponseEntity<?> postRemarque(@PathVariable Long id, @RequestBody UpdateAthleteObservationRequest request, HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null || !tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden: token user id does not match path id"));
        }
        return ResponseEntity.ok(athleteMapper.toDto(athleteService.updateObservation(id, request)));
    }

    // ATHLETE : get equipe detail
    @GetMapping("/{id}/equipe")
    public ResponseEntity<?> getEquipe(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null || !tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden: token user id does not match path id"));
        }

        // Récupère l'équipe avec le nouveau DTO
        EquipeDto equipe = athleteService.getEquipeForAthlete(id);
        if (equipe == null) {
            return ResponseEntity.ok(Map.of(
                    "id", null,
                    "nom", null,
                    "pays", null,
                    "athleteIdUsernameMap", Map.of()
            ));
        }

        // Vérifie et complète les usernames manquants depuis auth-service
        Map<Long, String> updatedMap = equipe.getAthleteIdUsernameMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String username = entry.getValue();
                            if (username == null || username.isBlank()) {
                                username = fetchUsernameFromAuth(entry.getKey());
                                if (username != null && !username.isBlank()) {
                                    athleteService.updateUsernameIfMissing(entry.getKey(), username);
                                } else {
                                    username = "";
                                }
                            }
                            return username;
                        }
                ));
        equipe.setAthleteIdUsernameMap(updatedMap);

        return ResponseEntity.ok(equipe);
    }

    private String fetchUsernameFromAuth(Long athleteId) {
        if (athleteId == null) return null;
        try {
            String url = authServiceBaseUrl + "/auth/user/" + athleteId;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Object username = response != null ? response.get("username") : null;
            return username != null ? String.valueOf(username) : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7);
        return jwtUtils.getUserIdFromJwtToken(token);
    }
}