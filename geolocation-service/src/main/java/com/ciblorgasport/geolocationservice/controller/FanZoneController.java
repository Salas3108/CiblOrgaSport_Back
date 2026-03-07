package com.ciblorgasport.geolocationservice.controller;

import com.ciblorgasport.geolocationservice.dto.FanZoneRequest;
import com.ciblorgasport.geolocationservice.dto.FanZoneResponse;
import com.ciblorgasport.geolocationservice.service.FanZoneBusinessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/geo/fanzones")
public class FanZoneController {

    private final FanZoneBusinessService fanZoneService;

    public FanZoneController(FanZoneBusinessService fanZoneService) {
        this.fanZoneService = fanZoneService;
    }

    /**
     * Crée une fan zone.
     * Accès : ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FanZoneResponse> create(@Valid @RequestBody FanZoneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fanZoneService.create(request));
    }

    /**
     * Liste toutes les fan zones.
     * Accès : PUBLIC.
     */
    @GetMapping
    public ResponseEntity<List<FanZoneResponse>> findAll() {
        return ResponseEntity.ok(fanZoneService.findAll());
    }

    /**
     * Retourne les fan zones dans un rayon donné, triées par distance croissante.
     * Accès : PUBLIC.
     *
     * @param lat    latitude du point de référence
     * @param lng    longitude du point de référence
     * @param rayon  rayon en mètres
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<FanZoneResponse>> findNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "500") double rayon) {

        return ResponseEntity.ok(fanZoneService.findNearby(lat, lng, rayon));
    }

    /**
     * Supprime une fan zone.
     * Accès : ADMIN.
     */
    @DeleteMapping("/{fanzoneId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long fanzoneId) {
        fanZoneService.delete(fanzoneId);
        return ResponseEntity.noContent().build();
    }
}
