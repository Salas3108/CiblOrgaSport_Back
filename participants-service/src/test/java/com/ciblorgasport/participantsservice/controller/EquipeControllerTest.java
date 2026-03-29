package com.ciblorgasport.participantsservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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

import com.ciblorgasport.participantsservice.dto.EquipeDto;
import com.ciblorgasport.participantsservice.dto.EquipeMapper;
import com.ciblorgasport.participantsservice.dto.request.AssignAthletesRequest;
import com.ciblorgasport.participantsservice.dto.request.CreateEquipeRequest;
import com.ciblorgasport.participantsservice.dto.request.UpdateEquipeRequest;
import com.ciblorgasport.participantsservice.model.Equipe;
import com.ciblorgasport.participantsservice.service.EquipeService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EquipeControllerTest {

    @Mock
    private EquipeService equipeService;

    @Mock
    private EquipeMapper equipeMapper;

    @InjectMocks
    private EquipeController controller;

    private EquipeDto buildDto(Long id, String nom) {
        EquipeDto dto = new EquipeDto();
        dto.setId(id);
        dto.setNom(nom);
        return dto;
    }

    private Equipe buildEntity(Long id, String nom) {
        Equipe e = new Equipe();
        e.setId(id);
        e.setNom(nom);
        return e;
    }

    @Test
    void getAll_returns_list() {
        Equipe entity = buildEntity(1L, "Team A");
        EquipeDto dto = buildDto(1L, "Team A");

        when(equipeService.findAll()).thenReturn(List.of(entity));
        when(equipeMapper.toDto(entity)).thenReturn(dto);

        ResponseEntity<List<EquipeDto>> response = controller.getAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Team A", response.getBody().get(0).getNom());
    }

    @Test
    void getOne_returns_equipe_dto() {
        Equipe entity = buildEntity(2L, "Team B");
        EquipeDto dto = buildDto(2L, "Team B");

        when(equipeService.findByIdOrThrow(2L)).thenReturn(entity);
        when(equipeMapper.toDto(entity)).thenReturn(dto);

        ResponseEntity<EquipeDto> response = controller.getOne(2L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2L, response.getBody().getId());
    }

    @Test
    void create_returns_equipe_dto() {
        CreateEquipeRequest request = new CreateEquipeRequest();
        Equipe entity = buildEntity(3L, "Team C");
        EquipeDto dto = buildDto(3L, "Team C");

        when(equipeService.create(any(CreateEquipeRequest.class))).thenReturn(entity);
        when(equipeMapper.toDto(entity)).thenReturn(dto);

        ResponseEntity<EquipeDto> response = controller.create(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3L, response.getBody().getId());
    }

    @Test
    void update_returns_equipe_dto() {
        UpdateEquipeRequest request = new UpdateEquipeRequest();
        Equipe entity = buildEntity(4L, "Team D");
        EquipeDto dto = buildDto(4L, "Team D");

        when(equipeService.update(eq(4L), any(UpdateEquipeRequest.class))).thenReturn(entity);
        when(equipeMapper.toDto(entity)).thenReturn(dto);

        ResponseEntity<EquipeDto> response = controller.update(4L, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(4L, response.getBody().getId());
    }

    @Test
    void delete_returns_200_with_message() {
        doNothing().when(equipeService).delete(5L);

        ResponseEntity<?> response = controller.delete(5L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void assignAthletes_returns_equipe_dto() {
        AssignAthletesRequest request = new AssignAthletesRequest();
        Equipe entity = buildEntity(6L, "Team F");
        EquipeDto dto = buildDto(6L, "Team F");

        when(equipeService.assignAthletes(eq(6L), any(AssignAthletesRequest.class))).thenReturn(entity);
        when(equipeMapper.toDto(entity)).thenReturn(dto);

        ResponseEntity<EquipeDto> response = controller.assignAthletes(6L, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(6L, response.getBody().getId());
    }
}
