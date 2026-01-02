package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Competition;
import com.ciblorgasport.eventservice.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/competitions")
public class CompetitionController {
    @Autowired
    private CompetitionRepository competitionRepository;

    @GetMapping
    public List<Competition> getAllCompetitions() {
        return competitionRepository.findAll();
    }

    @PostMapping
    public Competition createCompetition(@RequestBody Competition competition) {
        return competitionRepository.save(competition);
    }

    @GetMapping("/{id}")
    public Competition getCompetitionById(@PathVariable Long id) {
        return competitionRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Competition updateCompetition(@PathVariable Long id, @RequestBody Competition competitionDetails) {
        Competition competition = competitionRepository.findById(id).orElse(null);
        if (competition != null) {
            competition.setName(competitionDetails.getName());
            competition.setDate(competitionDetails.getDate());
            competition.setType(competitionDetails.getType());
            return competitionRepository.save(competition);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteCompetition(@PathVariable Long id) {
        competitionRepository.deleteById(id);
    }
}
