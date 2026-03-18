package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Competition;
import com.ciblorgasport.eventservice.model.Event;
import com.ciblorgasport.eventservice.repository.CompetitionRepository;
import com.ciblorgasport.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/competitions")
// SUPPRIMEZ ou MODIFIEZ la ligne ci-dessous :
// @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
@PreAuthorize("isAuthenticated()") // Tout utilisateur connecté peut voir
public class CompetitionController {
    
    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping
    public List<Competition> getAllCompetitions() {
        return competitionRepository.findAll().stream()
            .map(this::sanitizeCompetitionResponse)
            .collect(Collectors.toList());
    }

    // Gardez @PreAuthorize("hasRole('ADMIN')") sur les méthodes POST/PUT/DELETE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Competition createCompetition(@RequestBody Competition competition) {
        normalizeEventAssociation(competition);
        Competition saved = competitionRepository.save(competition);
        return sanitizeCompetitionResponse(saved);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Competition getCompetitionById(@PathVariable Long id) {
        return competitionRepository.findById(id)
            .map(this::sanitizeCompetitionResponse)
            .orElse(null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Competition updateCompetition(@PathVariable Long id, @RequestBody Competition competitionDetails) {
        Competition competition = competitionRepository.findById(id).orElse(null);
        if (competition != null) {
            competition.setEvent(competitionDetails.getEvent());
            competition.setDiscipline(competitionDetails.getDiscipline());
            normalizeEventAssociation(competition);
            Competition saved = competitionRepository.save(competition);
            return sanitizeCompetitionResponse(saved);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCompetition(@PathVariable Long id) {
        competitionRepository.deleteById(id);
    }

    private void normalizeEventAssociation(Competition competition) {
        if (competition == null || competition.getEvent() == null) {
            return;
        }
        Long eventId = competition.getEvent().getId();
        if (eventId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "event.id is required when event is provided");
        }
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event not found with id " + eventId));
        competition.setEvent(event);
    }

    private Competition sanitizeCompetitionResponse(Competition source) {
        if (source == null) {
            return null;
        }
        Competition response = new Competition();
        response.setId(source.getId());
        response.setDiscipline(source.getDiscipline());
        if (source.getEvent() != null && source.getEvent().getId() != null) {
            Event event = new Event();
            event.setId(source.getEvent().getId());
            response.setEvent(event);
        }
        return response;
    }
}