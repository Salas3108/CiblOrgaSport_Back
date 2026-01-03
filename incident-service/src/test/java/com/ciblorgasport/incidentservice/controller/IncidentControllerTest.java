package com.ciblorgasport.incidentservice.controller;

import com.ciblorgasport.incidentservice.model.*;
import com.ciblorgasport.incidentservice.service.IncidentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentControllerTest {

    @Mock
    private IncidentService incidentService;

    @InjectMocks
    private IncidentController incidentController;

    @Test
    void findAll_WithoutFilters_ReturnsAllIncidents() {
        // Arrange
        Incident incident1 = createTestIncident(1L, "Incident 1");
        Incident incident2 = createTestIncident(2L, "Incident 2");
        
        when(incidentService.findAll()).thenReturn(Arrays.asList(incident1, incident2));

        // Act
        ResponseEntity<List<Incident>> response = incidentController.findAll(null, null, null);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(incidentService, times(1)).findAll();
    }

    @Test
    void findAll_WithStatusFilter_ReturnsFiltered() {
        // Arrange
        Incident incident = createTestIncident(1L, "Active Incident");
        
        when(incidentService.search(IncidentStatus.ACTIF, null, null))
            .thenReturn(Arrays.asList(incident));

        // Act
        ResponseEntity<List<Incident>> response = incidentController.findAll(IncidentStatus.ACTIF, null, null);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(incidentService, times(1)).search(IncidentStatus.ACTIF, null, null);
    }

    @Test
    void findById_WhenExists_ReturnsIncident() {
        // Arrange
        Incident incident = createTestIncident(1L, "Test Incident");
        when(incidentService.findById(1L)).thenReturn(Optional.of(incident));

        // Act
        ResponseEntity<Incident> response = incidentController.findById(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Incident", response.getBody().getDescription());
    }

    @Test
    void findById_WhenNotExists_ReturnsNotFound() {
        // Arrange
        when(incidentService.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Incident> response = incidentController.findById(1L);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void create_ValidIncident_ReturnsCreatedIncident() {
        // Arrange
        Incident incidentToCreate = new Incident();
        incidentToCreate.setDescription("New Incident");
        
        Incident createdIncident = createTestIncident(1L, "New Incident");
        when(incidentService.create(any(Incident.class))).thenReturn(createdIncident);

        // Act
        ResponseEntity<Incident> response = incidentController.create(incidentToCreate);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void update_WhenExists_ReturnsUpdated() {
        // Arrange
        Incident updateDetails = new Incident();
        updateDetails.setDescription("Updated");
        
        Incident updatedIncident = createTestIncident(1L, "Updated");
        when(incidentService.update(eq(1L), any(Incident.class))).thenReturn(updatedIncident);

        // Act
        ResponseEntity<Incident> response = incidentController.update(1L, updateDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated", response.getBody().getDescription());
    }

    @Test
    void update_WhenNotExists_ReturnsNotFound() {
        // Arrange
        when(incidentService.update(eq(1L), any(Incident.class)))
            .thenThrow(new IllegalArgumentException("Not found"));

        // Act
        ResponseEntity<Incident> response = incidentController.update(1L, new Incident());

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void delete_Always_ReturnsNoContent() {
        // Act
        ResponseEntity<Void> response = incidentController.delete(1L);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(incidentService, times(1)).delete(1L);
    }

    private Incident createTestIncident(Long id, String description) {
        Incident incident = new Incident();
        incident.setId(id);
        incident.setDescription(description);
        incident.setStatus(IncidentStatus.ACTIF);
        incident.setType(IncidentType.TECHNIQUE);
        incident.setImpactLevel(ImpactLevel.MOYEN);
        incident.setLocation("Location");
        incident.setReportedBy("user123");
        incident.setReportedAt(LocalDateTime.now());
        return incident;
    }
}