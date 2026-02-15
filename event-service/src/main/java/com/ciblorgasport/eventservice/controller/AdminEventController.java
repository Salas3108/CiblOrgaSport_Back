package com.ciblorgasport.eventservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.eventservice.model.Competition;
import com.ciblorgasport.eventservice.model.Epreuve;
import com.ciblorgasport.eventservice.model.Event;
import com.ciblorgasport.eventservice.repository.CompetitionRepository;
import com.ciblorgasport.eventservice.repository.EpreuveRepository;
import com.ciblorgasport.eventservice.kafka.ResultatValideEventProducer;
import com.ciblorgasport.eventservice.repository.EventRepository;

@RestController
@RequestMapping("/admin/events")
@PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")

public class AdminEventController {
    private final EventRepository eventRepo;
    private final CompetitionRepository competitionRepo;
    private final EpreuveRepository epreuveRepo;
    private final ResultatValideEventProducer resultatProducer;

    @Autowired
    public AdminEventController(EventRepository eventRepo, CompetitionRepository competitionRepo, EpreuveRepository epreuveRepo, ResultatValideEventProducer resultatProducer) {
        this.eventRepo = eventRepo;
        this.competitionRepo = competitionRepo;
        this.epreuveRepo = epreuveRepo;
        this.resultatProducer = resultatProducer;
    }

    // EVENTS
    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventRepo.save(event);
    }

    @GetMapping
    public List<Event> getEvents() {
        return eventRepo.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventRepo.deleteById(id);
    }

    // COMPETITIONS
    @PostMapping("/{eventId}/competitions")
    public Competition addCompetition(@PathVariable Long eventId, @RequestBody Competition competition) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        competition.setEvent(event);
        return competitionRepo.save(competition);
    }

    // EPREUVES
    @PostMapping("/competitions/{competitionId}/epreuves")
    public Epreuve addEpreuve(@PathVariable Long competitionId, @RequestBody Epreuve epreuve) {
        Competition competition = competitionRepo.findById(competitionId).orElseThrow(() -> new RuntimeException("Competition not found"));
        epreuve.setCompetition(competition);
        return epreuveRepo.save(epreuve);
    }

    @PostMapping("/competitions/{competitionId}/epreuves/{epreuveId}/validate")
    public void validateResult(@PathVariable Long competitionId, @PathVariable Long epreuveId, @RequestBody(required = false) java.util.Map<String, Object> payload) {
        // minimal endpoint to allow publishing a ResultatValideEvent
        Long validatedBy = null;
        String message = "Résultat validé";
        if (payload != null) {
            Object v = payload.get("validatedBy");
            if (v instanceof Number) validatedBy = ((Number) v).longValue();
            Object m = payload.get("message");
            if (m instanceof String) message = (String) m;
        }
        java.util.UUID competitionUuid = null;
        // try to obtain competition uuid if competition entity has id mapping to UUID or just send null
        try {
            Competition comp = competitionRepo.findById(competitionId).orElse(null);
            if (comp != null && comp.getId() != null) {
                // competition id is Long in this model; forward as null in UUID field
            }
        } catch (Exception ignored) {}

        // publish event
        resultatProducer.publishResultat(competitionUuid, epreuveId, message, validatedBy);
    }
}
