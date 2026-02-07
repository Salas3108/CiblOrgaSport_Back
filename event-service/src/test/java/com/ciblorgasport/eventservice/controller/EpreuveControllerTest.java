package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Epreuve;
import com.ciblorgasport.eventservice.repository.EpreuveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EpreuveControllerTest {

    @Mock
    private EpreuveRepository epreuveRepository;

    @InjectMocks
    private EpreuveController epreuveController;

    @Test
    void getAllEpreuves_ShouldReturnList() {
        // Arrange
        Epreuve epreuve1 = new Epreuve();
        epreuve1.setId(1L);
        Epreuve epreuve2 = new Epreuve();
        epreuve2.setId(2L);
        
        when(epreuveRepository.findAll()).thenReturn(Arrays.asList(epreuve1, epreuve2));

        // Act
        List<Epreuve> result = epreuveController.getAllEpreuves();

        // Assert
        assertEquals(2, result.size());
        verify(epreuveRepository, times(1)).findAll();
    }

    @Test
    void createEpreuve_ShouldSaveEpreuve() {
        // Arrange
        Epreuve epreuve = new Epreuve();
        epreuve.setNom("Finale 100m");
        
        Epreuve savedEpreuve = new Epreuve();
        savedEpreuve.setId(1L);
        savedEpreuve.setNom("Finale 100m");
        
        when(epreuveRepository.save(epreuve)).thenReturn(savedEpreuve);

        // Act
        ResponseEntity<Epreuve> result = epreuveController.createEpreuve(epreuve);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        verify(epreuveRepository, times(1)).save(epreuve);
    }

    @Test
    void getEpreuveById_WhenExists() {
        // Arrange
        Epreuve epreuve = new Epreuve();
        epreuve.setId(1L);
        epreuve.setNom("Semi-finale");
        
        when(epreuveRepository.findById(1L)).thenReturn(Optional.of(epreuve));

        // Act
        ResponseEntity<Epreuve> result = epreuveController.getEpreuveById(1L);

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
        
        Epreuve updateDetails = new Epreuve();
        updateDetails.setNom("New Name");
        updateDetails.setDescription("New Description");
        
        when(epreuveRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(epreuveRepository.save(any(Epreuve.class))).thenReturn(existing);

        // Act
        ResponseEntity<Epreuve> result = epreuveController.updateEpreuve(1L, updateDetails);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getBody());
        verify(epreuveRepository, times(1)).save(any(Epreuve.class));
    }

    @Test
    void deleteEpreuve_ShouldCallDelete() {
        // Act
        epreuveController.deleteEpreuve(1L);

        // Assert
        verify(epreuveRepository, times(1)).deleteById(1L);
    }
}