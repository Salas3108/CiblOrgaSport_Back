package com.ciblorgasport.incidentservice.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ciblorgasport.incidentservice.client.LieuServiceClient;
import com.ciblorgasport.incidentservice.dto.IncidentDTO;
import com.ciblorgasport.incidentservice.dto.IncidentMapper;
import com.ciblorgasport.incidentservice.model.ImpactLevel;
import com.ciblorgasport.incidentservice.model.Incident;
import com.ciblorgasport.incidentservice.model.IncidentStatus;
import com.ciblorgasport.incidentservice.model.IncidentType;
import com.ciblorgasport.incidentservice.service.IncidentService;

@RestController("incidentControllerApi")
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final IncidentMapper incidentMapper;
    private final LieuServiceClient lieuServiceClient;

    public IncidentController(IncidentService incidentService, IncidentMapper incidentMapper, LieuServiceClient lieuServiceClient) {
        this.incidentService = incidentService;
        this.incidentMapper = incidentMapper;
        this.lieuServiceClient = lieuServiceClient;
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
    public ResponseEntity<List<IncidentDTO>> findAll(
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) IncidentType type,
            @RequestParam(required = false) ImpactLevel impact
    ) {
        if (status == null && type == null && impact == null) {
            return ResponseEntity.ok(incidentService.findAll().stream().map(incidentMapper::toDto).toList());
        }
        return ResponseEntity.ok(incidentService.search(status, type, impact).stream().map(incidentMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentDTO> findById(@PathVariable Long id) {
        return incidentService.findById(id)
                .map(i -> ResponseEntity.ok(incidentMapper.toDto(i)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<IncidentDTO> create(@RequestBody IncidentDTO incidentDto) {

        String currentUsername = getCurrentUsername();
        Incident incident = incidentMapper.toEntity(incidentDto);
        validateLieuExists(incident.getLieuId());
        incident.setReportedBy(currentUsername);
        if (incident.getReportedAt() == null) incident.setReportedAt(LocalDateTime.now());
        if (incident.getStatus() == null) incident.setStatus(IncidentStatus.ACTIF);
        Incident created = incidentService.create(incident);
        return ResponseEntity.ok(incidentMapper.toDto(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<IncidentDTO> update(@PathVariable Long id, @RequestBody IncidentDTO incidentDto) {
        try {
            Incident incident = incidentMapper.toEntity(incidentDto);
            validateLieuExists(incident.getLieuId());
            Incident updated = incidentService.update(id, incident);
            return ResponseEntity.ok(incidentMapper.toDto(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    private void validateLieuExists(Long lieuId) {
        if (lieuId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lieuId is required");
        }
        if (lieuId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lieuId must be a positive id");
        }

        try {
            if (!lieuServiceClient.existsById(lieuId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lieu not found with id " + lieuId);
            }
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to validate lieu with lieu-service", ex);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        incidentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}