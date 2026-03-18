package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Competition;
import com.ciblorgasport.eventservice.model.Event;
import com.ciblorgasport.eventservice.model.enums.Discipline;
import com.ciblorgasport.eventservice.repository.CompetitionRepository;
import com.ciblorgasport.eventservice.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetitionControllerTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CompetitionController competitionController;

    @Test
    void getAllCompetitions_ShouldReturnAll() {
        // Arrange
        Competition comp1 = new Competition();
        comp1.setId(1L);
        Competition comp2 = new Competition();
        comp2.setId(2L);
        
        when(competitionRepository.findAll()).thenReturn(Arrays.asList(comp1, comp2));

        // Act
        List<Competition> result = competitionController.getAllCompetitions();

        // Assert
        assertEquals(2, result.size());
        verify(competitionRepository, times(1)).findAll();
    }

    @Test
    void createCompetition_ShouldSaveCompetition() {
        // Arrange
        Competition competition = new Competition();
        competition.setDiscipline(Discipline.NATATION);
        
        Competition savedCompetition = new Competition();
        savedCompetition.setId(1L);
        savedCompetition.setDiscipline(Discipline.NATATION);
        
        when(competitionRepository.save(competition)).thenReturn(savedCompetition);

        // Act
        Competition result = competitionController.createCompetition(competition);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(competitionRepository, times(1)).save(competition);
    }

    @Test
    void getCompetitionById_WhenExists() {
        // Arrange
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setDiscipline(Discipline.EAU_LIBRE);
        
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        // Act
        Competition result = competitionController.getCompetitionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Discipline.EAU_LIBRE, result.getDiscipline());
        verify(competitionRepository, times(1)).findById(1L);
    }

    @Test
    void updateCompetition_ShouldUpdateFields() {
        // Arrange
        Competition existing = new Competition();
        existing.setId(1L);
        existing.setDiscipline(Discipline.NATATION);
        
        Competition updateDetails = new Competition();
        Event event = new Event();
        event.setId(10L);
        updateDetails.setEvent(event);
        updateDetails.setDiscipline(Discipline.WATER_POLO);
        
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(competitionRepository.save(any(Competition.class))).thenReturn(existing);

        // Act
        Competition result = competitionController.updateCompetition(1L, updateDetails);

        // Assert
        assertNotNull(result);
        assertEquals(Discipline.WATER_POLO, existing.getDiscipline());
        assertNotNull(existing.getEvent());
        assertEquals(10L, existing.getEvent().getId());
        verify(competitionRepository, times(1)).save(any(Competition.class));
    }

    @Test
    void deleteCompetition_ShouldCallDelete() {
        // Act
        competitionController.deleteCompetition(1L);

        // Assert
        verify(competitionRepository, times(1)).deleteById(1L);
    }
}