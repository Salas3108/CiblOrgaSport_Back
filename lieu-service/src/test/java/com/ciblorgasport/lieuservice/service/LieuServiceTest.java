package com.ciblorgasport.lieuservice.service;

import com.ciblorgasport.lieuservice.model.Lieu;
import com.ciblorgasport.lieuservice.repository.LieuRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LieuServiceTest {

    @Mock
    private LieuRepository lieuRepository;

    @InjectMocks
    private LieuService lieuService;

    @Test
    void getAllLieux_ShouldReturnAll() {
        Lieu lieu1 = new Lieu();
        lieu1.setId(1L);
        Lieu lieu2 = new Lieu();
        lieu2.setId(2L);

        when(lieuRepository.findAll()).thenReturn(Arrays.asList(lieu1, lieu2));

        List<Lieu> result = lieuService.getAllLieux();

        assertEquals(2, result.size());
        verify(lieuRepository, times(1)).findAll();
    }

    @Test
    void getLieuById_WhenExists_ShouldReturnLieu() {
        Lieu lieu = new Lieu();
        lieu.setId(1L);
        lieu.setNom("Stade de France");

        when(lieuRepository.findById(1L)).thenReturn(Optional.of(lieu));

        Lieu result = lieuService.getLieuById(1L);

        assertNotNull(result);
        assertEquals("Stade de France", result.getNom());
        verify(lieuRepository, times(1)).findById(1L);
    }

    @Test
    void getLieuById_WhenNotExists_ShouldReturnNull() {
        when(lieuRepository.findById(99L)).thenReturn(Optional.empty());

        Lieu result = lieuService.getLieuById(99L);

        assertNull(result);
        verify(lieuRepository, times(1)).findById(99L);
    }

    @Test
    void createLieu_ShouldSaveLieu() {
        Lieu toSave = new Lieu();
        toSave.setNom("Parc des Princes");

        Lieu saved = new Lieu();
        saved.setId(1L);
        saved.setNom("Parc des Princes");

        when(lieuRepository.save(toSave)).thenReturn(saved);

        Lieu result = lieuService.createLieu(toSave);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(lieuRepository, times(1)).save(toSave);
    }

    @Test
    void updateLieu_WhenExists_ShouldUpdateAllFields() {
        Lieu existing = new Lieu();
        existing.setId(1L);
        existing.setNom("Old");

        Lieu details = new Lieu();
        details.setNom("New Name");
        details.setAdresse("New Address");
        details.setVille("Paris");
        details.setCodePostal("75001");
        details.setPays("France");
        details.setCapaciteSpectateurs(90000);

        when(lieuRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(lieuRepository.save(existing)).thenReturn(existing);

        Lieu result = lieuService.updateLieu(1L, details);

        assertNotNull(result);
        assertEquals("New Name", result.getNom());
        assertEquals("New Address", result.getAdresse());
        assertEquals("Paris", result.getVille());
        assertEquals("75001", result.getCodePostal());
        assertEquals("France", result.getPays());
        assertEquals(90000, result.getCapaciteSpectateurs());
        verify(lieuRepository, times(1)).findById(1L);
        verify(lieuRepository, times(1)).save(existing);
    }

    @Test
    void updateLieu_WhenNotExists_ShouldReturnNull() {
        when(lieuRepository.findById(99L)).thenReturn(Optional.empty());

        Lieu result = lieuService.updateLieu(99L, new Lieu());

        assertNull(result);
        verify(lieuRepository, times(1)).findById(99L);
        verify(lieuRepository, never()).save(any(Lieu.class));
    }

    @Test
    void deleteLieu_ShouldCallDeleteById() {
        lieuService.deleteLieu(1L);

        verify(lieuRepository, times(1)).deleteById(1L);
    }
}
