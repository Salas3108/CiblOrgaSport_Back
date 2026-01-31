package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Event;
import com.ciblorgasport.eventservice.repository.EventRepository;
import com.ciblorgasport.eventservice.dto.EventDTO;
import com.ciblorgasport.eventservice.dto.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/events", "/api/events"})
@PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")

public class EventController {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @GetMapping
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream().map(eventMapper::toDto).toList();
    }

    @PostMapping
    public EventDTO createEvent(@RequestBody EventDTO eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        Event saved = eventRepository.save(event);
        return eventMapper.toDto(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(event -> ResponseEntity.ok(eventMapper.toDto(event)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDto) {
        return eventRepository.findById(id).map(event -> {
            eventMapper.updateEntityFromDto(event, eventDto);
            Event updated = eventRepository.save(event);
            return ResponseEntity.ok(eventMapper.toDto(updated));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
    }
}
