package com.ciblorgasport.resultatsservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.ciblorgasport.resultatsservice.dto.ResultatDto;
import com.ciblorgasport.resultatsservice.dto.ResultatMapper;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.service.ResultatService;

@ExtendWith(MockitoExtension.class)
class PublicResultatControllerTest {

    @Mock
    private ResultatService resultatService;

    @Mock
    private ResultatMapper resultatMapper;

    @InjectMocks
    private PublicResultatController controller;

    @Test
    void getClassementEpreuve_returns_published_list() {
        Resultat entity = new Resultat();
        entity.setId(1L);
        ResultatDto dto = new ResultatDto();
        dto.setId(1L);

        when(resultatService.getClassementEpreuve(10L, true)).thenReturn(List.of(entity));
        when(resultatMapper.toDto(eq(entity))).thenReturn(dto);

        ResponseEntity<List<ResultatDto>> response = controller.getClassementEpreuve(10L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getResultatsAthlete_returns_published_list() {
        Resultat entity = new Resultat();
        entity.setId(2L);
        ResultatDto dto = new ResultatDto();
        dto.setId(2L);

        when(resultatService.getResultatsAthlete(7L, true)).thenReturn(List.of(entity));
        when(resultatMapper.toDto(eq(entity))).thenReturn(dto);

        ResponseEntity<List<ResultatDto>> response = controller.getResultatsAthlete(7L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getResultatsEquipe_returns_published_list() {
        Resultat entity = new Resultat();
        entity.setId(3L);
        ResultatDto dto = new ResultatDto();
        dto.setId(3L);

        when(resultatService.getResultatsEquipe(9L, true)).thenReturn(List.of(entity));
        when(resultatMapper.toDto(eq(entity))).thenReturn(dto);

        ResponseEntity<List<ResultatDto>> response = controller.getResultatsEquipe(9L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }
}
