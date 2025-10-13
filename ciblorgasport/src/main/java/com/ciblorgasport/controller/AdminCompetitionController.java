package com.ciblorgasport.controller;

import com.ciblorgasport.dto.CompetitionResponse;
import com.ciblorgasport.dto.CreateCompetitionRequest;
import com.ciblorgasport.service.CompetitionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/competitions")
public class AdminCompetitionController {

    private final CompetitionService competitionService;

    public AdminCompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CompetitionResponse> create(@Valid @RequestBody CreateCompetitionRequest request) {
        CompetitionResponse created = competitionService.createCompetition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
