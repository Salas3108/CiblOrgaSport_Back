package com.ciblorgasport.participantsservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        byte[] fakeCert = new byte[] {1,2,3};
        byte[] fakePass = new byte[] {4,5,6};
        Athlete a1 = new Athlete(1L, "Dupont", "Marie", LocalDate.parse("2000-03-22"), "Belgique", false,
            new AthleteDocs(fakeCert, fakePass), "");
        Athlete a2 = new Athlete(2L, "Titouche", "Salim", LocalDate.parse("2000-07-26"), "Algerie", true,
            null, "OK");

        List<Athlete> fakeList = List.of(a1, a2);
        AthleteService athleteService = new AthleteService(null, null, null, null) {
            @Override public List<Athlete> findAll() { return fakeList; }
        };

        CommissaireController controller = new CommissaireController(athleteService, new AthleteMapper(), new MessageMapper());

        ResponseEntity<List<com.ciblorgasport.participantsservice.dto.AthleteDto>> response = controller.getAllAthletes();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals("Dupont", response.getBody().get(0).getNom());
        assertNotNull(response.getBody().get(0).getDocs().getCertificatMedicalUrl());
        assertNotNull(response.getBody().get(0).getDocs().getPassportUrl());
        assertEquals(2L, response.getBody().get(1).getId());
    }

    @Test
    void getValidatedAthletes_returns_only_validated_athletes() {
        Athlete validated = new Athlete(2L, "Titouche", "Salim", LocalDate.parse("2000-07-26"), "Algerie", true,
                null, "OK");

        List<Athlete> fakeValidated = List.of(validated);
        AthleteService athleteService = new AthleteService(null, null, null, null) {
            @Override public List<Athlete> findValidated() { return fakeValidated; }
        };

        CommissaireController controller = new CommissaireController(athleteService, new AthleteMapper(), new MessageMapper());

        ResponseEntity<List<com.ciblorgasport.participantsservice.dto.AthleteDto>> response = controller.getValidatedAthletes();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(2L, response.getBody().get(0).getId());
        assertEquals(true, response.getBody().get(0).isValide());
    }
}
