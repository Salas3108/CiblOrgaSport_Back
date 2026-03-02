package com.ciblorgasport.participantsservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.ciblorgasport.participantsservice.dto.AthleteMapper;
import com.ciblorgasport.participantsservice.dto.MessageMapper;
import com.ciblorgasport.participantsservice.model.Athlete;
import com.ciblorgasport.participantsservice.model.AthleteDocs;
import com.ciblorgasport.participantsservice.service.AthleteService;

class CommissaireControllerListAthletesTest {

    @Test
    void getAllAthletes_returns_list_of_dtos() {
        AthleteService athleteService = mock(AthleteService.class);
        AthleteMapper athleteMapper = new AthleteMapper();
        MessageMapper messageMapper = mock(MessageMapper.class);

        CommissaireController controller = new CommissaireController(athleteService, athleteMapper, messageMapper);

        Athlete a1 = new Athlete(1L, "Dupont", "Marie", LocalDate.parse("2000-03-22"), "Belgique", false,
                new AthleteDocs("certificat.pdf", "passport.pdf"), "");
        Athlete a2 = new Athlete(2L, "Titouche", "Salim", LocalDate.parse("2000-07-26"), "Algerie", true,
                null, "OK");

        when(athleteService.findAll()).thenReturn(List.of(a1, a2));

        ResponseEntity<List<com.ciblorgasport.participantsservice.dto.AthleteDto>> response = controller.getAllAthletes();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals("Dupont", response.getBody().get(0).getNom());
        assertEquals(2L, response.getBody().get(1).getId());
    }

    @Test
    void getValidatedAthletes_returns_only_validated_athletes() {
        AthleteService athleteService = mock(AthleteService.class);
        AthleteMapper athleteMapper = new AthleteMapper();
        MessageMapper messageMapper = mock(MessageMapper.class);

        CommissaireController controller = new CommissaireController(athleteService, athleteMapper, messageMapper);

        Athlete validated = new Athlete(2L, "Titouche", "Salim", LocalDate.parse("2000-07-26"), "Algerie", true,
                null, "OK");

        when(athleteService.findValidated()).thenReturn(List.of(validated));

        ResponseEntity<List<com.ciblorgasport.participantsservice.dto.AthleteDto>> response = controller.getValidatedAthletes();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(2L, response.getBody().get(0).getId());
        assertEquals(true, response.getBody().get(0).isValide());
    }
}
