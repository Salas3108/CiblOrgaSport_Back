package com.ciblorgasport.geolocationservice.controller;

import com.ciblorgasport.geolocationservice.dto.PositionRequest;
import com.ciblorgasport.geolocationservice.dto.PositionResponse;
import com.ciblorgasport.geolocationservice.service.AthletePositionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/geo")
public class AthletePositionController {

    private final AthletePositionService positionService;

    public AthletePositionController(AthletePositionService positionService) {
        this.positionService = positionService;
    }

    /** Enregistre la position GPS de l'athlète (son propre ID uniquement via JWT). */
    @PostMapping("/athletes/{athleteId}/position")
    @PreAuthorize("hasRole('ATHLETE')")
    public ResponseEntity<PositionResponse> recordPosition(
            @PathVariable Long athleteId,
            @Valid @RequestBody PositionRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);
        return ResponseEntity.ok(positionService.recordPosition(athleteId, request, token));
    }

    /** Dernière position connue d'un athlète. Accès : COMMISSAIRE, ADMIN. */
    @GetMapping("/athletes/{athleteId}/position")
    @PreAuthorize("hasAnyRole('COMMISSAIRE', 'ADMIN')")
    public ResponseEntity<PositionResponse> getLastPosition(@PathVariable Long athleteId) {
        return ResponseEntity.ok(positionService.getLastPosition(athleteId));
    }

    /** Historique des positions d'un athlète entre deux dates. Accès : COMMISSAIRE, ADMIN. */
    @GetMapping("/athletes/{athleteId}/history")
    @PreAuthorize("hasAnyRole('COMMISSAIRE', 'ADMIN')")
    public ResponseEntity<List<PositionResponse>> getHistory(
            @PathVariable Long athleteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        return ResponseEntity.ok(positionService.getHistory(athleteId, dateDebut, dateFin));
    }

    /** Supprime toutes les positions d'un athlète (RGPD). Accès : ADMIN. */
    @DeleteMapping("/athletes/{athleteId}/positions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePositions(@PathVariable Long athleteId) {
        positionService.deletePositions(athleteId);
        return ResponseEntity.noContent().build();
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
