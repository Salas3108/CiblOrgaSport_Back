package com.ciblorgasport.resultatservice;

import com.ciblorgasport.resultatservice.dto.CreerResultatRequest;
import com.ciblorgasport.resultatservice.dto.ResultatDTO;
import com.ciblorgasport.resultatservice.entity.Resultat;
import com.ciblorgasport.resultatservice.entity.StatusResultat;
import com.ciblorgasport.resultatservice.repository.ResultatRepository;
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
        CreerResultatRequest request = CreerResultatRequest.builder()
                .epreuveId(1L)
                .athleteId(5L)
                .classement(1)
                .temps(45.32)
                .distance(100.50)
                .points(100.0)
                .observations("Test observation")
                .build();
        
        Resultat resultatEntity = Resultat.builder()
                .id(1L)
                .epreuveId(request.getEpreuveId())
                .athleteId(request.getAthleteId())
                .classement(request.getClassement())
                .temps(request.getTemps())
                .distance(request.getDistance())
                .points(request.getPoints())
                .observations(request.getObservations())
                .saisieParId(commissaireId)
                .status(StatusResultat.SAISI)
                .build();
        
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
        CreerResultatRequest request = CreerResultatRequest.builder()
                .epreuveId(1L)
                .athleteId(5L)
                .classement(1)
                .build();
        
        Resultat existingResultat = new Resultat();
        when(resultatRepository.findByEpreuveIdAndAthleteId(request.getEpreuveId(), request.getAthleteId()))
                .thenReturn(Optional.of(existingResultat));
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            resultatService.saisirResultat(request, commissaireId);
        });
    }
}
