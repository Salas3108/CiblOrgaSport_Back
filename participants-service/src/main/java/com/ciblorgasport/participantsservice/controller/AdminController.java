package com.ciblorgasport.participantsservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.participantsservice.dto.AthleteMapper;
import com.ciblorgasport.participantsservice.dto.request.CreateAthleteRequest;
import com.ciblorgasport.participantsservice.security.JwtUtils;
import com.ciblorgasport.participantsservice.service.AthleteService;

/**
 * Endpoints cote ADMIN pour la creation d'athletes.
 */
@RestController
@RequestMapping("/api/athlete")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AthleteService athleteService;
    private final AthleteMapper athleteMapper;
    private final JwtUtils jwtUtils;

    public AdminController(AthleteService athleteService, AthleteMapper athleteMapper, JwtUtils jwtUtils) {
        this.athleteService = athleteService;
        this.athleteMapper = athleteMapper;
        this.jwtUtils = jwtUtils;
    }

    // ADMIN : create profile for a specific athlete (idempotent)
    @PostMapping("/{idAthlete}")
    public ResponseEntity<?> createForAthlete(@PathVariable Long idAthlete,
                                              @RequestBody CreateAthleteRequest request,
                                              HttpServletRequest httpRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "unauthenticated"));
        }
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "missing userId in token"));
        }
        if (request == null || request.getUsername() == null || request.getUsername().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "username est obligatoire"));
        }
        var athlete = athleteService.createIfMissingForUser(idAthlete, request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(athleteMapper.toDto(athlete));
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7);
        return jwtUtils.getUserIdFromJwtToken(token);
    }
}
