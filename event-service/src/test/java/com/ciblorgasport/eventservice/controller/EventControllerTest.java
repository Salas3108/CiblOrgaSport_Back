package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Event;
import com.ciblorgasport.eventservice.repository.EventRepository;
import com.ciblorgasport.eventservice.dto.EventDTO;
import com.ciblorgasport.eventservice.dto.EventMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventController eventController;

    @Test
    void getAllEvents_ShouldReturnAllEvents() {
        // Arrange
        Event event1 = new Event();
        event1.setId(1L);
        event1.setName("Event 1");
        
        Event event2 = new Event();
        event2.setId(2L);
        event2.setName("Event 2");
        
        List<Event> events = Arrays.asList(event1, event2);
        when(eventRepository.findAll()).thenReturn(events);

        // Act
        List<Event> result = eventController.getAllEvents();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Event 1", result.get(0).getName());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void getEventById_WhenEventExists_ShouldReturnEvent() {
        // Arrange
        Event event = new Event();
        event.setId(1L);
        event.setName("Test Event");
        
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        // Act
        Event result = eventController.getEventById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Event", result.getName());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void getEventById_WhenEventNotExists_ShouldReturnNull() {
        // Arrange
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Event result = eventController.getEventById(1L);

        // Assert
        assertNull(result);
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void createEvent_ShouldSaveAndReturnEvent() {
        // Arrange
        EventDTO dto = new EventDTO();
        dto.setName("New Event");
        dto.setDateDebut(LocalDate.now());
        dto.setDateFin(LocalDate.now().plusDays(2));
        dto.setDescription("Description");
        dto.setPaysHote("France");

        Event entity = new Event();
        entity.setName(dto.getName());
        entity.setDateDebut(dto.getDateDebut());
        entity.setDateFin(dto.getDateFin());
        entity.setDescription(dto.getDescription());
        entity.setPaysHote(dto.getPaysHote());

        Event savedEvent = new Event();
        savedEvent.setId(1L);
        savedEvent.setName("New Event");

        EventDTO savedDto = new EventDTO();
        savedDto.setId(1L);
        savedDto.setName("New Event");

        when(eventMapper.toEntity(dto)).thenReturn(entity);
        when(eventRepository.save(entity)).thenReturn(savedEvent);
        when(eventMapper.toDto(savedEvent)).thenReturn(savedDto);

        // Act
        ResponseEntity<EventDTO> response = eventController.createEvent(dto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(eventRepository, times(1)).save(entity);
    }

    @Test
    void updateEvent_WhenEventExists_ShouldUpdateEvent() {
        // Arrange
        Event existingEvent = new Event();
        existingEvent.setId(1L);
        existingEvent.setName("Old Name");
        
        Event updateDetails = new Event();
        updateDetails.setName("Updated Name");
        updateDetails.setDateDebut(LocalDate.now());
        updateDetails.setDateFin(LocalDate.now().plusDays(1));
        updateDetails.setDescription("Updated description");
        updateDetails.setPaysHote("Maroc");
        
        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(existingEvent);

        // Act
        Event result = eventController.updateEvent(1L, updateDetails);

        // Assert
        assertNotNull(result);
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void updateEvent_WhenEventNotExists_ShouldReturnNull() {
        // Arrange
        Event updateDetails = new Event();
        updateDetails.setName("Updated Name");
        
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Event result = eventController.updateEvent(1L, updateDetails);

        // Assert
        assertNull(result);
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void deleteEvent_ShouldCallDelete() {
        // Act
        eventController.deleteEvent(1L);

        // Assert
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void createEvent_WhenDataIntegrityViolation_ShouldReturnConflict() {
        EventDTO dto = new EventDTO();
        dto.setName("Test Event");

        when(eventMapper.toEntity(dto)).thenReturn(new Event());
        when(eventRepository.save(any(Event.class))).thenThrow(new DataIntegrityViolationException("Unique constraint"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> eventController.createEvent(dto));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(ex.getReason().toLowerCase().contains("data integrity"));
    }
}