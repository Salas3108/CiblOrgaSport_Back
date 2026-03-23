package com.ciblorgasport.participantsservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;

import com.ciblorgasport.participantsservice.dto.ForfaitResponse;
import com.ciblorgasport.participantsservice.dto.request.AssignAthletesRequest;
import com.ciblorgasport.participantsservice.dto.request.ForfaitRequest;
import com.ciblorgasport.participantsservice.model.StatutParticipation;
import com.ciblorgasport.participantsservice.service.EpreuveAssignmentService;

@RestController
@RequestMapping({"/commissaire/epreuves", "/api/commissaire/epreuves"})
public class EpreuveAssignmentController {

    private final EpreuveAssignmentService assignmentService;

    public EpreuveAssignmentController(EpreuveAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/assignments")
    public ResponseEntity<Map<Long, List<Long>>> listAssignments() {
        return ResponseEntity.ok(assignmentService.listAllAssignments());
    }

    @GetMapping("/{epreuveId}/athletes")
    public ResponseEntity<Map<String, Object>> listAthletes(@PathVariable Long epreuveId) {
        List<Long> athleteIds = assignmentService.listAthletesForEpreuve(epreuveId);
        return ResponseEntity.ok(Map.of("epreuveId", epreuveId, "athleteIds", athleteIds));
    }

    @PostMapping("/{epreuveId}/athletes")
    public ResponseEntity<Map<String, Object>> assignAthletes(@PathVariable Long epreuveId,
                                                              @RequestBody AssignAthletesRequest request) {
        List<Long> athleteIds = assignmentService.assignAthletes(epreuveId, request);
        return ResponseEntity.ok(Map.of("epreuveId", epreuveId, "athleteIds", athleteIds));
    }

    @GetMapping("/{epreuveId}/athletes/{athleteId}/statut")
    public ResponseEntity<Map<String, Object>> getStatutParticipation(@PathVariable Long epreuveId,
                                                                       @PathVariable Long athleteId) {
        StatutParticipation statut = assignmentService.getStatutParticipation(epreuveId, athleteId);
        return ResponseEntity.ok(Map.of("epreuveId", epreuveId, "athleteId", athleteId, "statut", statut.name()));
    }

    @PostMapping("/{epreuveId}/athletes/{athleteId}/forfait")
    @PreAuthorize("hasRole('COMMISSAIRE') or hasRole('ADMIN')")
    public ResponseEntity<ForfaitResponse> declarerForfait(@PathVariable Long epreuveId,
                                                           @PathVariable Long athleteId,
                                                           @RequestBody(required = false) ForfaitRequest request) {
        return ResponseEntity.ok(assignmentService.declarerForfait(epreuveId, athleteId, request));
    }
}
