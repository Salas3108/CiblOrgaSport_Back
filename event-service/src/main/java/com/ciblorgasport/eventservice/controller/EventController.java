package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Event;
import com.ciblorgasport.eventservice.repository.EventRepository;
import com.ciblorgasport.eventservice.dto.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/events", "/api/events"})
public class EventController {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event updateDetails) {
        return eventRepository.findById(id)
                .map(existing -> {
                    existing.setName(updateDetails.getName());
                    existing.setDate(updateDetails.getDate());
                    // ...apply other updatable fields as needed...
                    return eventRepository.save(existing);
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
    }
}
