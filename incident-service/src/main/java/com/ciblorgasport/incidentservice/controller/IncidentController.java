package com.ciblorgasport.incidentservice.controller;

import com.ciblorgasport.incidentservice.model.Incident;
import com.ciblorgasport.incidentservice.model.IncidentStatus;
import com.ciblorgasport.incidentservice.model.IncidentType;
import com.ciblorgasport.incidentservice.model.ImpactLevel;
import com.ciblorgasport.incidentservice.service.IncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("incidentControllerApi")
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // Méthode utilitaire pour obtenir le username de manière sécurisée
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system"; // Valeur par défaut pour les tests
    }

    @GetMapping
    public ResponseEntity<List<Incident>> findAll(
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) IncidentType type,
            @RequestParam(required = false) ImpactLevel impact
    ) {
        if (status == null && type == null && impact == null) {
            return ResponseEntity.ok(incidentService.findAll());
        }
        return ResponseEntity.ok(incidentService.search(status, type, impact));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> findById(@PathVariable Long id) {
        return incidentService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<Incident> create(@RequestBody Incident incident) {
        // Utiliser la méthode utilitaire
        String currentUsername = getCurrentUsername();
        
        incident.setReportedBy(currentUsername);
        
        if (incident.getReportedAt() == null) {
            incident.setReportedAt(LocalDateTime.now());
        }
        
        if (incident.getStatus() == null) {
            incident.setStatus(IncidentStatus.ACTIF);
        }
        
        Incident created = incidentService.create(incident);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<Incident> update(@PathVariable Long id, @RequestBody Incident incident) {
        try {
            Incident updated = incidentService.update(id, incident);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        incidentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}