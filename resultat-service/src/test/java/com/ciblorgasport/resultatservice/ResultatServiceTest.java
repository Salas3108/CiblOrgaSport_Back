package com.ciblorgasport.resultatservice;

import com.ciblorgasport.resultatservice.dto.CreerResultatRequest;
import com.ciblorgasport.resultatservice.dto.ResultatDTO;
import com.ciblorgasport.resultatservice.entity.Resultat;
import com.ciblorgasport.resultatservice.entity.StatusResultat;
import com.ciblorgasport.resultatservice.repository.ResultatRepository;
import com.ciblorgasport.resultatservice.repository.HistoriqueResultatRepository;
import com.ciblorgasport.resultatservice.service.ResultatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ResultatServiceTest {
    
    @Mock
    private ResultatRepository resultatRepository;
    @Mock
    private HistoriqueResultatRepository historiqueResultatRepository;
    
    @InjectMocks
    private ResultatService resultatService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testSaisirResultat() {
        // Arrange
        Long commissaireId = 10L;
        CreerResultatRequest request = new CreerResultatRequest();
        request.setEpreuveId(1L);
        request.setAthleteId(5L);
        request.setClassement(1);
        request.setTemps(45.32);
        request.setDistance(100.50);
        request.setPoints(100.0);
        request.setObservations("Test observation");
        
        Resultat resultatEntity = new Resultat();
        resultatEntity.setId(1L);
        resultatEntity.setEpreuveId(request.getEpreuveId());
        resultatEntity.setAthleteId(request.getAthleteId());
        resultatEntity.setClassement(request.getClassement());
        resultatEntity.setTemps(request.getTemps());
        resultatEntity.setDistance(request.getDistance());
        resultatEntity.setPoints(request.getPoints());
        resultatEntity.setObservations(request.getObservations());
        resultatEntity.setSaisieParId(commissaireId);
        resultatEntity.setStatus(StatusResultat.SAISI);
        
        when(resultatRepository.findByEpreuveIdAndAthleteId(request.getEpreuveId(), request.getAthleteId()))
                .thenReturn(Optional.empty());
        when(resultatRepository.save(any(Resultat.class)))
                .thenReturn(resultatEntity);
        
        // Act
        ResultatDTO resultat = resultatService.saisirResultat(request, commissaireId);
        
        // Assert
        assertNotNull(resultat);
        assertEquals(1L, resultat.getId());
        assertEquals(StatusResultat.SAISI, resultat.getStatus());
        assertEquals(commissaireId, resultat.getSaisieParId());
    }
    
    @Test
    public void testSaisirResultat_Duplicate() {
        // Arrange
        Long commissaireId = 10L;
        CreerResultatRequest request = new CreerResultatRequest();
        request.setEpreuveId(1L);
        request.setAthleteId(5L);
        request.setClassement(1);
        
        Resultat existingResultat = new Resultat();
        when(resultatRepository.findByEpreuveIdAndAthleteId(request.getEpreuveId(), request.getAthleteId()))
                .thenReturn(Optional.of(existingResultat));
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            resultatService.saisirResultat(request, commissaireId);
        });
    }
}
