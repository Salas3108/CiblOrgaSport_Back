package com.ciblorgasport.geolocationservice.controller;

import com.ciblorgasport.geolocationservice.dto.AthleteGeoConfigRequest;
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

    /**
     * Enregistre la position GPS d'un athlète.
     * Accès : ATHLETE (son propre ID uniquement, vérifié dans le service via JWT userId).
     */
    @PostMapping("/athletes/{athleteId}/position")
    @PreAuthorize("hasRole('ATHLETE')")
    public ResponseEntity<PositionResponse> recordPosition(
            @PathVariable Long athleteId,
            @Valid @RequestBody PositionRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);
        PositionResponse response = positionService.recordPosition(athleteId, request, token);
        return ResponseEntity.ok(response);
    }

    /**
     * Retourne la dernière position connue d'un athlète.
     * Accès : COMMISSAIRE, ADMIN.
     */
    @GetMapping("/athletes/{athleteId}/position")
    @PreAuthorize("hasAnyRole('COMMISSAIRE', 'ADMIN')")
    public ResponseEntity<PositionResponse> getLastPosition(@PathVariable Long athleteId) {
        return ResponseEntity.ok(positionService.getLastPosition(athleteId));
    }

    /**
     * Retourne les positions actuelles de tous les athlètes d'une épreuve en cours.
     * Accès : COMMISSAIRE, ADMIN.
     */
    @GetMapping("/epreuves/{epreuveId}/athletes/positions")
    @PreAuthorize("hasAnyRole('COMMISSAIRE', 'ADMIN')")
    public ResponseEntity<List<PositionResponse>> getEpreuvePositions(@PathVariable Long epreuveId) {
        return ResponseEntity.ok(positionService.getEpreuvePositions(epreuveId));
    }

    /**
     * Retourne l'historique des positions d'un athlète entre deux dates.
     * Accès : COMMISSAIRE, ADMIN.
     */
    @GetMapping("/athletes/{athleteId}/history")
    @PreAuthorize("hasAnyRole('COMMISSAIRE', 'ADMIN')")
    public ResponseEntity<List<PositionResponse>> getHistory(
            @PathVariable Long athleteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        return ResponseEntity.ok(positionService.getHistory(athleteId, dateDebut, dateFin));
    }

    /**
     * Supprime toutes les positions d'un athlète (nettoyage post-compétition / RGPD).
     * Accès : ADMIN.
     */
    @DeleteMapping("/athletes/{athleteId}/positions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePositions(@PathVariable Long athleteId) {
        positionService.deletePositions(athleteId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Crée ou met à jour la configuration de géolocalisation d'un athlète.
     * Accès : ADMIN.
     */
    @PatchMapping("/admin/athletes/{athleteId}/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> upsertConfig(
            @PathVariable Long athleteId,
            @RequestBody AthleteGeoConfigRequest configRequest) {

        positionService.upsertConfig(athleteId, configRequest.isGeolocActive(), configRequest.getNom());
        return ResponseEntity.ok().build();
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
