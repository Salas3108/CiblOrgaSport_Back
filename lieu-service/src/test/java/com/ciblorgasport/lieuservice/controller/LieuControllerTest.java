package com.ciblorgasport.lieuservice.controller;

import com.ciblorgasport.lieuservice.model.Lieu;
import com.ciblorgasport.lieuservice.service.LieuService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LieuControllerTest {

    @Mock
    private LieuService lieuService;

    @InjectMocks
    private LieuController lieuController;

    @Test
    void getAllLieux_ShouldReturnAll() {
        // Arrange
        Lieu lieu1 = new Lieu();
        lieu1.setId(1L);
        Lieu lieu2 = new Lieu();
        lieu2.setId(2L);

        when(lieuService.getAllLieux()).thenReturn(Arrays.asList(lieu1, lieu2));

        // Act
        List<Lieu> result = lieuController.getAllLieux();

        // Assert
        assertEquals(2, result.size());
        verify(lieuService, times(1)).getAllLieux();
    }

    @Test
    void getLieuById_WhenExists() {
        // Arrange
        Lieu lieu = new Lieu();
        lieu.setId(1L);
        lieu.setNom("Stade de France");

        when(lieuService.getLieuById(1L)).thenReturn(lieu);

        // Act
        Lieu result = lieuController.getLieuById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Stade de France", result.getNom());
        verify(lieuService, times(1)).getLieuById(1L);
    }

    @Test
    void getLieuById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(lieuService.getLieuById(99L)).thenReturn(null);

        // Act
        Lieu result = lieuController.getLieuById(99L);

        // Assert
        assertNull(result);
        verify(lieuService, times(1)).getLieuById(99L);
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

        when(lieuService.createLieu(lieu)).thenReturn(savedLieu);

        // Act
        Lieu result = lieuController.createLieu(lieu);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(lieuService, times(1)).createLieu(lieu);
    }

    @Test
    void updateLieu_ShouldUpdateAllFields() {
        // Arrange
        Lieu updateDetails = new Lieu();
        updateDetails.setNom("New Name");
        updateDetails.setAdresse("New Address");
        updateDetails.setVille("New City");
        updateDetails.setCodePostal("75000");
        updateDetails.setPays("France");
        updateDetails.setCapaciteSpectateurs(12000);

        Lieu updated = new Lieu();
        updated.setId(1L);
        updated.setCapaciteSpectateurs(12000);

        when(lieuService.updateLieu(1L, updateDetails)).thenReturn(updated);

        // Act
        Lieu result = lieuController.updateLieu(1L, updateDetails);

        // Assert
        assertNotNull(result);
        assertEquals(12000, result.getCapaciteSpectateurs());
        verify(lieuService, times(1)).updateLieu(1L, updateDetails);
    }

    @Test
    void updateLieu_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(lieuService.updateLieu(eq(99L), any(Lieu.class))).thenReturn(null);

        // Act
        Lieu result = lieuController.updateLieu(99L, new Lieu());

        // Assert
        assertNull(result);
        verify(lieuService, times(1)).updateLieu(eq(99L), any(Lieu.class));
    }

    @Test
    void deleteLieu_ShouldCallDelete() {
        // Act
        lieuController.deleteLieu(1L);

        // Assert
        verify(lieuService, times(1)).deleteLieu(1L);
    }
}
