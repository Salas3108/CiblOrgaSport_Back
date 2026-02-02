package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Competition;
import com.ciblorgasport.eventservice.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/competitions")
// SUPPRIMEZ ou MODIFIEZ la ligne ci-dessous :
// @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
@PreAuthorize("isAuthenticated()") // Tout utilisateur connecté peut voir
public class CompetitionController {
    
    @Autowired
    private CompetitionRepository competitionRepository;

    @GetMapping
    public List<Competition> getAllCompetitions() {
        return competitionRepository.findAll();
    }

    // Gardez @PreAuthorize("hasRole('ADMIN')") sur les méthodes POST/PUT/DELETE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Competition createCompetition(@RequestBody Competition competition) {
        return competitionRepository.save(competition);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Competition getCompetitionById(@PathVariable Long id) {
        return competitionRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Competition updateCompetition(@PathVariable Long id, @RequestBody Competition competitionDetails) {
        Competition competition = competitionRepository.findById(id).orElse(null);
        if (competition != null) {
            competition.setName(competitionDetails.getName());
            competition.setDateDebut(competitionDetails.getDateDebut());
            competition.setDateFin(competitionDetails.getDateFin());
            competition.setType(competitionDetails.getType());
            return competitionRepository.save(competition);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCompetition(@PathVariable Long id) {
        competitionRepository.deleteById(id);
    }
}