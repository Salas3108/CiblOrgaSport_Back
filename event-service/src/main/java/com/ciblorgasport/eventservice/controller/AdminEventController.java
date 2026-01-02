package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Event;
import com.ciblorgasport.eventservice.model.Competition;
import com.ciblorgasport.eventservice.model.Epreuve;
import com.ciblorgasport.eventservice.repository.EventRepository;
import com.ciblorgasport.eventservice.repository.CompetitionRepository;
import com.ciblorgasport.eventservice.repository.EpreuveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventRepository eventRepo;
    private final CompetitionRepository competitionRepo;
    private final EpreuveRepository epreuveRepo;

    @Autowired
    public AdminEventController(EventRepository eventRepo, CompetitionRepository competitionRepo, EpreuveRepository epreuveRepo) {
        this.eventRepo = eventRepo;
        this.competitionRepo = competitionRepo;
        this.epreuveRepo = epreuveRepo;
    }

    // EVENTS
    @PostMapping("/admin/events")
    public Event createEvent(@RequestBody Event event) {
        return eventRepo.save(event);
    }

    @GetMapping("/admin/events")
    public List<Event> getEvents() {
        return eventRepo.findAll();
    }

    @DeleteMapping("/admin/events/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventRepo.deleteById(id);
    }

    // COMPETITIONS
    @PostMapping("/admin/events/{eventId}/competitions")
    public Competition addCompetition(@PathVariable Long eventId, @RequestBody Competition competition) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        competition.setEvent(event);
        return competitionRepo.save(competition);
    }

    // EPREUVES
    @PostMapping("/admin/events/competitions/{competitionId}/epreuves")
    public Epreuve addEpreuve(@PathVariable Long competitionId, @RequestBody Epreuve epreuve) {
        Competition competition = competitionRepo.findById(competitionId).orElseThrow(() -> new RuntimeException("Competition not found"));
        epreuve.setCompetition(competition);
        return epreuveRepo.save(epreuve);
    }
}
