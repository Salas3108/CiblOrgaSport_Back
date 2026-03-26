package com.ciblorgasport.participantsservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.participantsservice.dto.ForfaitResponse;
import com.ciblorgasport.participantsservice.dto.request.ForfaitRequest;
import com.ciblorgasport.participantsservice.service.EpreuveAssignmentService;

@RestController
@RequestMapping("/epreuves")
public class ParticipationController {

    private final EpreuveAssignmentService assignmentService;

    public ParticipationController(EpreuveAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/{epreuveId}/athletes/{athleteId}/forfait")
    public ResponseEntity<ForfaitResponse> declarerForfait(
            @PathVariable Long epreuveId,
            @PathVariable Long athleteId,
            @RequestBody(required = false) ForfaitRequest request) {
        ForfaitResponse response = assignmentService.declarerForfait(epreuveId, athleteId, request);
        return ResponseEntity.ok(response);
    }
}
