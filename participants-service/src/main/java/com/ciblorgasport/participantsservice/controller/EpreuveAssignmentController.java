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

import com.ciblorgasport.participantsservice.dto.request.AssignAthletesRequest;
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

    @GetMapping("/{epreuveId}/athletes/{athleteId}/statut")
    public ResponseEntity<Map<String, Object>> getAthleteStatut(@PathVariable Long epreuveId,
                                                                @PathVariable Long athleteId) {
        return ResponseEntity.ok(assignmentService.getAthleteStatut(epreuveId, athleteId));
    }

    @PostMapping("/{epreuveId}/athletes")
    public ResponseEntity<Map<String, Object>> assignAthletes(@PathVariable Long epreuveId,
                                                              @RequestBody AssignAthletesRequest request) {
        List<Long> athleteIds = assignmentService.assignAthletes(epreuveId, request);
        return ResponseEntity.ok(Map.of("epreuveId", epreuveId, "athleteIds", athleteIds));
    }
}
