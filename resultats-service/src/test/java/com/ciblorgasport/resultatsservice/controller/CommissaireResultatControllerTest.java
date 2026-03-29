package com.ciblorgasport.resultatsservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.http.ResponseEntity;

import com.ciblorgasport.resultatsservice.dto.ResultatDto;
import com.ciblorgasport.resultatsservice.dto.ResultatMapper;
import com.ciblorgasport.resultatsservice.dto.request.ResultatRequest;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.service.ResultatService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommissaireResultatControllerTest {

    @Mock
    private ResultatService resultatService;

    @Mock
    private ResultatMapper resultatMapper;

    @Mock
    private com.ciblorgasport.resultatsservice.client.EventServiceClient eventServiceClient;

    @Mock
    private com.ciblorgasport.resultatsservice.client.ParticipantsServiceClient participantsServiceClient;

    @InjectMocks
    private CommissaireResultatController controller;

    @Test
    void createOrUpdate_returns_resultat_dto() {
        ResultatRequest request = new ResultatRequest();
        Resultat entity = new Resultat();
        entity.setId(1L);
        ResultatDto dto = new ResultatDto();
        dto.setId(1L);

        when(resultatService.createOrUpdate(any(ResultatRequest.class))).thenReturn(entity);
        when(resultatMapper.toDto(eq(entity))).thenReturn(dto);

        ResponseEntity<ResultatDto> response = controller.createOrUpdate(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void validate_returns_resultat_dto() {
        Resultat entity = new Resultat();
        entity.setId(2L);
        ResultatDto dto = new ResultatDto();
        dto.setId(2L);

        when(resultatService.validateResultat(2L)).thenReturn(entity);
        when(resultatMapper.toDto(eq(entity))).thenReturn(dto);

        ResponseEntity<ResultatDto> response = controller.validate(2L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2L, response.getBody().getId());
    }

    @Test
    void publish_returns_resultat_dto() {
        Resultat entity = new Resultat();
        entity.setId(3L);
        ResultatDto dto = new ResultatDto();
        dto.setId(3L);

        when(resultatService.publishResultat(3L)).thenReturn(entity);
        when(resultatMapper.toDto(eq(entity))).thenReturn(dto);

        ResponseEntity<ResultatDto> response = controller.publish(3L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3L, response.getBody().getId());
    }

    @Test
    void getClassementEpreuve_returns_list() {
        Resultat entity = new Resultat();
        entity.setId(4L);
        ResultatDto dto = new ResultatDto();
        dto.setId(4L);

        when(resultatService.getClassementEpreuve(10L, false)).thenReturn(List.of(entity));
        when(resultatMapper.toDto(eq(entity))).thenReturn(dto);

        ResponseEntity<List<ResultatDto>> response = controller.getClassementEpreuve(10L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }
}
