package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Epreuve;
import com.ciblorgasport.eventservice.dto.EpreuveDTO;
import com.ciblorgasport.eventservice.dto.EpreuveMapper;
import com.ciblorgasport.eventservice.repository.EpreuveRepository;
import com.ciblorgasport.eventservice.repository.CompetitionRepository;
import com.ciblorgasport.eventservice.repository.LieuRepository;
import com.ciblorgasport.eventservice.validator.EpreuveValidator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EpreuveControllerTest {

    @Mock
    private EpreuveRepository epreuveRepository;

    @Mock
    private EpreuveMapper epreuveMapper;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private LieuRepository lieuRepository;

    @Mock
    private EpreuveValidator epreuveValidator;

    @InjectMocks
    private EpreuveController epreuveController;

    @Test
    void getAllEpreuves_ShouldReturnList() {
        // Arrange
        Epreuve epreuve1 = new Epreuve();
        epreuve1.setId(1L);
        Epreuve epreuve2 = new Epreuve();
        epreuve2.setId(2L);

        EpreuveDTO dto1 = new EpreuveDTO();
        dto1.setId(1L);
        EpreuveDTO dto2 = new EpreuveDTO();
        dto2.setId(2L);

        when(epreuveRepository.findAll()).thenReturn(Arrays.asList(epreuve1, epreuve2));
        when(epreuveMapper.toDto(epreuve1)).thenReturn(dto1);
        when(epreuveMapper.toDto(epreuve2)).thenReturn(dto2);

        // Act
        List<EpreuveDTO> result = epreuveController.getAllEpreuves();

        // Assert
        assertEquals(2, result.size());
        verify(epreuveRepository, times(1)).findAll();
        verify(epreuveMapper, times(1)).toDto(epreuve1);
        verify(epreuveMapper, times(1)).toDto(epreuve2);
    }

    @Test
    void createEpreuve_ShouldSaveEpreuve() {
        // Arrange
        EpreuveDTO dto = new EpreuveDTO();
        dto.setNom("Finale 100m");

        Epreuve entity = new Epreuve();
        entity.setNom("Finale 100m");

        Epreuve savedEntity = new Epreuve();
        savedEntity.setId(1L);
        savedEntity.setNom("Finale 100m");

        EpreuveDTO savedDto = new EpreuveDTO();
        savedDto.setId(1L);
        savedDto.setNom("Finale 100m");

        when(epreuveMapper.toEntity(dto)).thenReturn(entity);
        when(epreuveRepository.save(entity)).thenReturn(savedEntity);
        when(epreuveMapper.toDto(savedEntity)).thenReturn(savedDto);

        // Act
        ResponseEntity<EpreuveDTO> result = epreuveController.createEpreuve(dto);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        verify(epreuveRepository, times(1)).save(entity);
        verify(epreuveValidator, times(1)).validate(dto);
    }

    @Test
    void getEpreuveById_WhenExists() {
        // Arrange
        Epreuve epreuve = new Epreuve();
        epreuve.setId(1L);
        epreuve.setNom("Semi-finale");

        EpreuveDTO dto = new EpreuveDTO();
        dto.setId(1L);
        dto.setNom("Semi-finale");

        when(epreuveRepository.findById(1L)).thenReturn(Optional.of(epreuve));
        when(epreuveMapper.toDto(epreuve)).thenReturn(dto);

        // Act
        ResponseEntity<EpreuveDTO> result = epreuveController.getEpreuveById(1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals("Semi-finale", result.getBody().getNom());
        verify(epreuveRepository, times(1)).findById(1L);
    }

    @Test
    void updateEpreuve_ShouldUpdateFields() {
        // Arrange
        Epreuve existing = new Epreuve();
        existing.setId(1L);
        existing.setNom("Old Name");

        EpreuveDTO updateDto = new EpreuveDTO();
        updateDto.setNom("New Name");
        updateDto.setDescription("New Description");

        when(epreuveRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(epreuveRepository.save(any(Epreuve.class))).thenReturn(existing);
        when(epreuveMapper.toDto(existing)).thenReturn(new EpreuveDTO());

        // Act
        ResponseEntity<EpreuveDTO> result = epreuveController.updateEpreuve(1L, updateDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getBody());
        verify(epreuveRepository, times(1)).save(any(Epreuve.class));
        verify(epreuveValidator, times(1)).validate(updateDto);
    }

    @Test
    void deleteEpreuve_ShouldCallDelete() {
        // Arrange
        when(epreuveRepository.existsById(1L)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = epreuveController.deleteEpreuve(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(epreuveRepository, times(1)).deleteById(1L);
    }

    @Test
    void addAthlete_ShouldAddAthlete() {
        // Arrange
        Epreuve existing = new Epreuve();
        existing.setId(1L);

        when(epreuveRepository.findById(1L)).thenReturn(Optional.of(existing));

        Epreuve saved = new Epreuve();
        saved.setId(1L);
        saved.setAthleteIds(new HashSet<>(Collections.singletonList(5L)));

        when(epreuveRepository.save(any(Epreuve.class))).thenReturn(saved);

        EpreuveDTO savedDto = new EpreuveDTO();
        savedDto.setId(1L);
        savedDto.setAthleteIds(new HashSet<>(Collections.singletonList(5L)));

        when(epreuveMapper.toDto(saved)).thenReturn(savedDto);

        // Act
        ResponseEntity<EpreuveDTO> result = epreuveController.addAthlete(1L, Collections.singletonMap("athleteId", 5L));

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getAthleteIds().contains(5L));
        verify(epreuveRepository, times(1)).save(any(Epreuve.class));
    }

    @Test
    void addAthlete_ShouldReturnBadRequestWhenMissingAthleteId() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> epreuveController.addAthlete(1L, Collections.emptyMap()));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void addAthlete_ShouldReturnNotFoundWhenEpreuveNotFound() {
        when(epreuveRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> epreuveController.addAthlete(1L, Collections.singletonMap("athleteId", 5L)));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getAthletes_ShouldReturnSet() {
        // Arrange
        Epreuve e = new Epreuve();
        e.setId(1L);
        e.setAthleteIds(new HashSet<>(Collections.singletonList(7L)));

        when(epreuveRepository.findById(1L)).thenReturn(Optional.of(e));

        // Act
        ResponseEntity<Set<Long>> response = epreuveController.getAthletes(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(7L));
        verify(epreuveRepository, times(1)).findById(1L);
    }

    @Test
    void getEpreuvesForAthlete_ShouldReturnList() {
        Epreuve epreuve1 = new Epreuve();
        epreuve1.setId(1L);
        Epreuve epreuve2 = new Epreuve();
        epreuve2.setId(2L);

        EpreuveDTO dto1 = new EpreuveDTO();
        dto1.setId(1L);
        EpreuveDTO dto2 = new EpreuveDTO();
        dto2.setId(2L);

        when(epreuveRepository.findByAthleteIdsContains(5L)).thenReturn(Arrays.asList(epreuve1, epreuve2));
        when(epreuveMapper.toDto(epreuve1)).thenReturn(dto1);
        when(epreuveMapper.toDto(epreuve2)).thenReturn(dto2);

        ResponseEntity<List<EpreuveDTO>> response = epreuveController.getEpreuvesForAthlete(5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(epreuveRepository, times(1)).findByAthleteIdsContains(5L);
    }

    @Test
    void isAthleteParticipating_WhenPresent() {
        // Arrange
        Epreuve e = new Epreuve();
        e.setId(1L);
        e.setAthleteIds(new HashSet<>(Collections.singletonList(5L)));

        when(epreuveRepository.findById(1L)).thenReturn(Optional.of(e));

        // Act
        ResponseEntity<Map<String, Boolean>> resp = epreuveController.isAthleteParticipating(1L, 5L);

        // Assert
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().get("participating"));
        verify(epreuveRepository, times(1)).findById(1L);
    }

    @Test
    void isAthleteParticipating_WhenNotPresent() {
        // Arrange
        Epreuve e = new Epreuve();
        e.setId(1L);
        e.setAthleteIds(new HashSet<>(Collections.singletonList(2L)));

        when(epreuveRepository.findById(1L)).thenReturn(Optional.of(e));

        // Act
        ResponseEntity<Map<String, Boolean>> resp = epreuveController.isAthleteParticipating(1L, 5L);

        // Assert
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertFalse(resp.getBody().get("participating"));
    }

    @Test
    void isAthleteParticipating_EpreuveNotFound() {
        when(epreuveRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> epreuveController.isAthleteParticipating(99L, 5L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}