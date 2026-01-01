package com.ciblorgasport.controller;

import com.ciblorgasport.entity.*;
import com.ciblorgasport.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")  
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventController {

    private final EventRepository eventRepo;
    private final CompetitionRepository competitionRepo;
    private final EpreuveRepository epreuveRepo;

    public AdminEventController(
            EventRepository eventRepo,
            CompetitionRepository competitionRepo,
            EpreuveRepository epreuveRepo) {
        this.eventRepo = eventRepo;
        this.competitionRepo = competitionRepo;
        this.epreuveRepo = epreuveRepo;
    }

    // EVENTS
    @PostMapping("/events")  // CHANGÉ ICI
    public Event createEvent(@RequestBody Event event) {
        return eventRepo.save(event);
    }

    @GetMapping("/events")  // CHANGÉ ICI
    public List<Event> getEvents() {
        return eventRepo.findAll();
    }

    @DeleteMapping("/events/{id}")  
    public void deleteEvent(@PathVariable UUID id) {
        eventRepo.deleteById(id);
    }

    // COMPETITIONS
    @PostMapping("/events/{eventId}/competitions")  
    public Competition addCompetition(
            @PathVariable UUID eventId,
            @RequestBody Competition competition) {

        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        competition.setEvent(event);
        return competitionRepo.save(competition);
    }

    // EPREUVES
    @PostMapping("/events/competitions/{competitionId}/epreuves")  
    public Epreuve addEpreuve(
            @PathVariable UUID competitionId,
            @RequestBody Epreuve epreuve) {

        Competition competition = competitionRepo.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("Competition not found"));

        epreuve.setCompetition(competition);
        return epreuveRepo.save(epreuve);
    }
}