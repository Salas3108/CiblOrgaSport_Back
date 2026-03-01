package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Lieu;
import com.ciblorgasport.eventservice.repository.LieuRepository;
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
class LieuControllerTest {

    @Mock
    private LieuRepository lieuRepository;

    @InjectMocks
    private LieuController lieuController;

    @Test
    void getAllLieux_ShouldReturnAll() {
        // Arrange
        Lieu lieu1 = new Lieu();
        lieu1.setId(1L);
        Lieu lieu2 = new Lieu();
        lieu2.setId(2L);
        
        when(lieuRepository.findAll()).thenReturn(Arrays.asList(lieu1, lieu2));

        // Act
        List<Lieu> result = lieuController.getAllLieux();

        // Assert
        assertEquals(2, result.size());
        verify(lieuRepository, times(1)).findAll();
    }

    @Test
    void getLieuById_WhenExists() {
        // Arrange
        Lieu lieu = new Lieu();
        lieu.setId(1L);
        lieu.setNom("Stade de France");
        
        when(lieuRepository.findById(1L)).thenReturn(Optional.of(lieu));

        // Act
        Lieu result = lieuController.getLieuById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Stade de France", result.getNom());
        verify(lieuRepository, times(1)).findById(1L);
    }

    @Test
    void createLieu_ShouldSaveLieu() {
        // Arrange
        Lieu lieu = new Lieu();
        lieu.setNom("Parc des Princes");
        lieu.setVille("Paris");
        
        Lieu savedLieu = new Lieu();
        savedLieu.setId(1L);
        savedLieu.setNom("Parc des Princes");
        
        when(lieuRepository.save(lieu)).thenReturn(savedLieu);

        // Act
        Lieu result = lieuController.createLieu(lieu);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(lieuRepository, times(1)).save(lieu);
    }

    @Test
    void updateLieu_ShouldUpdateAllFields() {
        // Arrange
        Lieu existing = new Lieu();
        existing.setId(1L);
        
        Lieu updateDetails = new Lieu();
        updateDetails.setNom("New Name");
        updateDetails.setAdresse("New Address");
        updateDetails.setVille("New City");
        updateDetails.setCodePostal("75000");
        updateDetails.setPays("France");
        updateDetails.setCapaciteSpectateurs(12000);
        
        when(lieuRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(lieuRepository.save(any(Lieu.class))).thenReturn(existing);

        // Act
        Lieu result = lieuController.updateLieu(1L, updateDetails);

        // Assert
        assertNotNull(result);
        assertEquals(12000, existing.getCapaciteSpectateurs());
        verify(lieuRepository, times(1)).save(any(Lieu.class));
    }

    @Test
    void deleteLieu_ShouldCallDelete() {
        // Act
        lieuController.deleteLieu(1L);

        // Assert
        verify(lieuRepository, times(1)).deleteById(1L);
    }
}