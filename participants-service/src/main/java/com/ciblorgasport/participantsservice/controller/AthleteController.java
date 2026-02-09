package com.ciblorgasport.participantsservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

import com.ciblorgasport.participantsservice.dto.AthleteMapper;
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "forbidden: token user id does not match path id"));
        }
        return ResponseEntity.ok(athleteMapper.toDto(athleteService.updateInfo(id, request)));
    }

    // ATHLETE : post doc
    @PostMapping("/{id}/doc")
    public ResponseEntity<?> postDoc(@PathVariable Long id, @RequestBody UpdateAthleteDocsRequest request, HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null || !tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "forbidden: token user id does not match path id"));
        }
        return ResponseEntity.ok(athleteMapper.toDto(athleteService.updateDocs(id, request)));
    }

    // ATHLETE : post remarque (observation)
    @PostMapping("/{id}/remarque")
    public ResponseEntity<?> postRemarque(@PathVariable Long id, @RequestBody UpdateAthleteObservationRequest request, HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null || !tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "forbidden: token user id does not match path id"));
        }
        return ResponseEntity.ok(athleteMapper.toDto(athleteService.updateObservation(id, request)));
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7);
        return jwtUtils.getUserIdFromJwtToken(token);
    }
}
