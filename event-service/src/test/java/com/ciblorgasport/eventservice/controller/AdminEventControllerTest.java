package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Event;
import com.ciblorgasport.eventservice.model.Competition;
import com.ciblorgasport.eventservice.model.Epreuve;
import com.ciblorgasport.eventservice.repository.EventRepository;
import com.ciblorgasport.eventservice.repository.CompetitionRepository;
import com.ciblorgasport.eventservice.repository.EpreuveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminEventControllerTest {

    @Mock
    private EventRepository eventRepository;
    
    @Mock
    private CompetitionRepository competitionRepository;
    
    @Mock
    private EpreuveRepository epreuveRepository;

    @InjectMocks
    private AdminEventController adminEventController;

    @Test
    void createEvent_ShouldSaveEvent() {
        // Arrange
        Event event = new Event();
        event.setName("Championnat");
        
        Event savedEvent = new Event();
        savedEvent.setId(1L);
        savedEvent.setName("Championnat");
        
        when(eventRepository.save(event)).thenReturn(savedEvent);

        // Act
        Event result = adminEventController.createEvent(event);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void getEvents_ShouldReturnAllEvents() {
        // Arrange
        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);
        
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2));

        // Act
        List<Event> result = adminEventController.getEvents();

        // Assert
        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void deleteEvent_ShouldCallDelete() {
        // Act
        adminEventController.deleteEvent(1L);

        // Assert
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void addCompetition_WhenEventExists() {
        // Arrange
        Event event = new Event();
        event.setId(1L);
        
        Competition competition = new Competition();
        competition.setName("100m");
        
        Competition savedCompetition = new Competition();
        savedCompetition.setId(1L);
        savedCompetition.setName("100m");
        savedCompetition.setEvent(event);
        
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(competitionRepository.save(any(Competition.class))).thenReturn(savedCompetition);

        // Act
        Competition result = adminEventController.addCompetition(1L, competition);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getEvent());
        assertEquals(1L, result.getEvent().getId());
        verify(competitionRepository, times(1)).save(any(Competition.class));
    }

    @Test
    void addCompetition_WhenEventNotExists_ShouldThrowException() {
        // Arrange
        Competition competition = new Competition();
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminEventController.addCompetition(1L, competition);
        });
        
        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    void addEpreuve_WhenCompetitionExists() {
        // Arrange
        Competition competition = new Competition();
        competition.setId(1L);
        
        Epreuve epreuve = new Epreuve();
        epreuve.setNom("Finale");
        
        Epreuve savedEpreuve = new Epreuve();
        savedEpreuve.setId(1L);
        savedEpreuve.setNom("Finale");
        savedEpreuve.setCompetition(competition);
        
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(epreuveRepository.save(any(Epreuve.class))).thenReturn(savedEpreuve);

        // Act
        Epreuve result = adminEventController.addEpreuve(1L, epreuve);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCompetition());
        assertEquals(1L, result.getCompetition().getId());
        verify(epreuveRepository, times(1)).save(any(Epreuve.class));
    }

    @Test
    void addEpreuve_WhenCompetitionNotExists_ShouldThrowException() {
        // Arrange
        Epreuve epreuve = new Epreuve();
        when(competitionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminEventController.addEpreuve(1L, epreuve);
        });
        
        assertEquals("Competition not found", exception.getMessage());
    }
}