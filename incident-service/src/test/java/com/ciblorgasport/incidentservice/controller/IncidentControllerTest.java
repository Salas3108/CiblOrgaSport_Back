package com.ciblorgasport.incidentservice.controller;

import com.ciblorgasport.incidentservice.model.*;
import com.ciblorgasport.incidentservice.client.LieuServiceClient;
import com.ciblorgasport.incidentservice.dto.IncidentDTO;
import com.ciblorgasport.incidentservice.dto.IncidentMapper;
import com.ciblorgasport.incidentservice.service.IncidentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

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

    @Mock
    private IncidentMapper incidentMapper;

    @Mock
    private LieuServiceClient lieuServiceClient;

    @InjectMocks
    private IncidentController incidentController;

    @Test
    void findAll_WithoutFilters_ReturnsAllIncidents() {
        // Arrange
        Incident incident1 = createTestIncident(1L, "Incident 1");
        Incident incident2 = createTestIncident(2L, "Incident 2");
        
        when(incidentService.findAll()).thenReturn(Arrays.asList(incident1, incident2));
        IncidentDTO dto1 = toDto(incident1);
        IncidentDTO dto2 = toDto(incident2);
        when(incidentMapper.toDto(incident1)).thenReturn(dto1);
        when(incidentMapper.toDto(incident2)).thenReturn(dto2);

        // Act
        ResponseEntity<List<IncidentDTO>> response = incidentController.findAll(null, null, null);

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
        IncidentDTO dto = toDto(incident);
        when(incidentMapper.toDto(incident)).thenReturn(dto);

        // Act
        ResponseEntity<List<IncidentDTO>> response = incidentController.findAll(IncidentStatus.ACTIF, null, null);

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

        when(incidentMapper.toDto(incident)).thenReturn(toDto(incident));

        // Act
        ResponseEntity<IncidentDTO> response = incidentController.findById(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Incident", response.getBody().getDescription());
    }

    @Test
    void findById_WhenNotExists_ReturnsNotFound() {
        // Arrange
        when(incidentService.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<IncidentDTO> response = incidentController.findById(1L);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void create_ValidIncident_ReturnsCreatedIncident() {
        // Arrange
        IncidentDTO incidentToCreateDto = new IncidentDTO();
        incidentToCreateDto.setDescription("New Incident");
        incidentToCreateDto.setLieuId(10L);

        Incident incidentEntity = new Incident();
        incidentEntity.setDescription("New Incident");
        incidentEntity.setLieuId(10L);

        Incident createdIncident = createTestIncident(1L, "New Incident");
        when(lieuServiceClient.existsById(10L)).thenReturn(true);
        when(incidentMapper.toEntity(any(IncidentDTO.class))).thenReturn(incidentEntity);
        when(incidentService.create(any(Incident.class))).thenReturn(createdIncident);
        when(incidentMapper.toDto(createdIncident)).thenReturn(toDto(createdIncident));

        // Act
        ResponseEntity<IncidentDTO> response = incidentController.create(incidentToCreateDto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void update_WhenExists_ReturnsUpdated() {
        // Arrange
        IncidentDTO updateDto = new IncidentDTO();
        updateDto.setDescription("Updated");
        updateDto.setLieuId(10L);

        Incident updateEntity = new Incident();
        updateEntity.setDescription("Updated");
        updateEntity.setLieuId(10L);

        Incident updatedIncident = createTestIncident(1L, "Updated");
        when(lieuServiceClient.existsById(10L)).thenReturn(true);
        when(incidentMapper.toEntity(any(IncidentDTO.class))).thenReturn(updateEntity);
        when(incidentService.update(eq(1L), any(Incident.class))).thenReturn(updatedIncident);
        when(incidentMapper.toDto(updatedIncident)).thenReturn(toDto(updatedIncident));

        // Act
        ResponseEntity<IncidentDTO> response = incidentController.update(1L, updateDto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated", response.getBody().getDescription());
    }

    @Test
    void update_WhenNotExists_ReturnsNotFound() {
        // Arrange
        when(lieuServiceClient.existsById(10L)).thenReturn(true);
        when(incidentService.update(eq(1L), any(Incident.class)))
            .thenThrow(new IllegalArgumentException("Not found"));

        // Act
        IncidentDTO dto = new IncidentDTO();
        dto.setLieuId(10L);
        Incident incident = new Incident();
        incident.setLieuId(10L);
        when(incidentMapper.toEntity(any(IncidentDTO.class))).thenReturn(incident);
        ResponseEntity<IncidentDTO> response = incidentController.update(1L, dto);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void create_WhenLieuIdMissing_ThrowsBadRequest() {
        IncidentDTO dto = new IncidentDTO();
        Incident incident = new Incident();
        incident.setDescription("Incident without lieu");
        when(incidentMapper.toEntity(any(IncidentDTO.class))).thenReturn(incident);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> incidentController.create(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void create_WhenLieuServiceUnavailable_ThrowsBadGateway() {
        IncidentDTO dto = new IncidentDTO();
        dto.setLieuId(10L);

        Incident incident = new Incident();
        incident.setDescription("Incident");
        incident.setLieuId(10L);

        when(incidentMapper.toEntity(any(IncidentDTO.class))).thenReturn(incident);
        when(lieuServiceClient.existsById(10L)).thenThrow(new IllegalStateException("timeout"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> incidentController.create(dto));

        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatusCode());
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
        incident.setLieuId(10L);
        incident.setReportedBy("user123");
        incident.setReportedAt(LocalDateTime.now());
        return incident;
    }

    private IncidentDTO toDto(Incident incident) {
        IncidentDTO dto = new IncidentDTO();
        dto.setId(incident.getId());
        dto.setDescription(incident.getDescription());
        dto.setImpactLevel(incident.getImpactLevel());
        dto.setType(incident.getType());
        dto.setLieuId(incident.getLieuId());
        dto.setStatus(incident.getStatus());
        dto.setReportedBy(incident.getReportedBy());
        dto.setReportedAt(incident.getReportedAt());
        dto.setUpdatedAt(incident.getUpdatedAt());
        dto.setResolvedAt(incident.getResolvedAt());
        return dto;
    }
}