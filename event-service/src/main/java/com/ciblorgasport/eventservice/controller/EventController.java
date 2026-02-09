package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Event;
import com.ciblorgasport.eventservice.repository.EventRepository;
import com.ciblorgasport.eventservice.dto.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import com.ciblorgasport.eventservice.repository.LieuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import com.ciblorgasport.eventservice.dto.EventDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping({"/events", "/api/events"})
public class EventController {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private LieuRepository lieuRepository;

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventDTO eventDto) {
        // validation simple côté contrôleur
        if (eventDto.getName() == null || eventDto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required field 'name'");
        }
        // valider le lieu si fourni
        if (eventDto.getLieuId() != null) {
            lieuRepository.findById(eventDto.getLieuId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lieu not found with id " + eventDto.getLieuId()));
        }

        Event entity = eventMapper.toEntity(eventDto);
        try {
            Event saved = eventRepository.save(entity);
            return new ResponseEntity<>(eventMapper.toDto(saved), HttpStatus.CREATED);
        } catch (DataIntegrityViolationException ex) {
            // renvoyer 409 Conflict avec message utile
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event creation failed: data integrity violation");
        } catch (Exception ex) {
            // message explicite pour faciliter le debug (global handler loggue déjà)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create event: " + (ex.getMessage() != null ? ex.getMessage() : ""));
        }
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
