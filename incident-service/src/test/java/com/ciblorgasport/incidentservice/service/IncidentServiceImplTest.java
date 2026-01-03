package com.ciblorgasport.incidentservice.service;

import com.ciblorgasport.incidentservice.model.*;
import com.ciblorgasport.incidentservice.repository.IncidentRepository;
import com.ciblorgasport.incidentservice.service.impl.IncidentServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceImplTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    @Test
    void findAll_ReturnsAllIncidents() {
        // Arrange
        Incident incident1 = new Incident();
        incident1.setId(1L);
        Incident incident2 = new Incident();
        incident2.setId(2L);
        
        when(incidentRepository.findAll()).thenReturn(Arrays.asList(incident1, incident2));

        // Act
        List<Incident> result = incidentService.findAll();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void findById_WhenExists_ReturnsIncident() {
        // Arrange
        Incident incident = new Incident();
        incident.setId(1L);
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        // Act
        Optional<Incident> result = incidentService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void create_SetsDefaultValues() {
        // Arrange
        Incident incident = new Incident();
        incident.setDescription("Test");
        
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> {
            Incident saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        Incident result = incidentService.create(incident);

        // Assert
        assertNotNull(result.getReportedAt());
        assertEquals(IncidentStatus.ACTIF, result.getStatus());
        assertNotNull(result.getId());
    }

    @Test
    void update_WhenExists_UpdatesFields() {
        // Arrange
        Incident existing = new Incident();
        existing.setId(1L);
        existing.setStatus(IncidentStatus.ACTIF);
        
        Incident update = new Incident();
        update.setDescription("Updated");
        update.setStatus(IncidentStatus.RESOLU);
        
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(incidentRepository.save(any(Incident.class))).thenReturn(existing);

        // Act
        Incident result = incidentService.update(1L, update);

        // Assert
        assertNotNull(result.getUpdatedAt());
        assertNotNull(result.getResolvedAt());
    }

    @Test
    void update_WhenNotExists_ThrowsException() {
        // Arrange
        when(incidentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            incidentService.update(1L, new Incident());
        });
    }

    @Test
    void delete_CallsRepository() {
        // Act
        incidentService.delete(1L);

        // Assert
        verify(incidentRepository, times(1)).deleteById(1L);
    }

    @Test
    void search_FiltersCorrectly() {
        // Arrange
        Incident incident1 = new Incident();
        incident1.setStatus(IncidentStatus.ACTIF);
        incident1.setType(IncidentType.TECHNIQUE);
        incident1.setImpactLevel(ImpactLevel.MOYEN);
        
        Incident incident2 = new Incident();
        incident2.setStatus(IncidentStatus.RESOLU);
        incident2.setType(IncidentType.SECURITE);
        incident2.setImpactLevel(ImpactLevel.CRITIQUE);
        
        when(incidentRepository.findAll()).thenReturn(Arrays.asList(incident1, incident2));

        // Act
        List<Incident> result = incidentService.search(
            IncidentStatus.ACTIF, 
            IncidentType.TECHNIQUE, 
            ImpactLevel.MOYEN
        );

        // Assert
        assertEquals(1, result.size());
    }
}