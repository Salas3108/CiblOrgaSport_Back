package com.ciblorgasport.controller;

import com.ciblorgasport.entity.Abonnement;
import com.ciblorgasport.repository.AbonnementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AbonnementController.class)
class AbonnementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AbonnementRepository abonnementRepository;

    @MockBean
    private RestTemplate restTemplate;

    private Long userId;
    private Long competitionId;
    private Abonnement abonnement;

    @BeforeEach
    void setUp() {
        userId = 1L;
        competitionId = 42L;
        abonnement = new Abonnement(userId, competitionId);
        abonnement.setId(UUID.randomUUID());
    }

    @Test
    void testGetMesAbonnements() throws Exception {
        List<Abonnement> abonnements = Arrays.asList(abonnement);
        when(abonnementRepository.findByUserId(userId)).thenReturn(abonnements);

        mockMvc.perform(get("/api/abonnements/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].competitionId").value(competitionId));
    }

    @Test
    void testGetMesAbonnements_Empty() throws Exception {
        when(abonnementRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/abonnements/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testSabonnerCompetition_Success() throws Exception {
        when(abonnementRepository.existsByUserIdAndCompetitionId(userId, competitionId)).thenReturn(false);
        when(abonnementRepository.save(any(Abonnement.class))).thenReturn(abonnement);

        mockMvc.perform(post("/api/abonnements/subscribe")
                .param("userId", userId.toString())
                .param("competitionId", competitionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Abonnement réussi"))
                .andExpect(jsonPath("$.competitionId").value(competitionId));
    }

    @Test
    void testSabonnerCompetition_AlreadySubscribed() throws Exception {
        when(abonnementRepository.existsByUserIdAndCompetitionId(userId, competitionId)).thenReturn(true);

        mockMvc.perform(post("/api/abonnements/subscribe")
                .param("userId", userId.toString())
                .param("competitionId", competitionId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Déjà abonné à cette compétition"));
    }

    @Test
    void testSabonnerCompetition_MissingUserId() throws Exception {
        mockMvc.perform(post("/api/abonnements/subscribe")
                .param("competitionId", competitionId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSabonnerCompetition_MissingCompetitionId() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/abonnements/subscribe")
                .param("userId", userId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSabonnerCompetition_InvalidLong() throws Exception {
        mockMvc.perform(post("/api/abonnements/subscribe")
                .param("userId", userId.toString())
                .param("competitionId", "invalid-long"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDesabonnerCompetition_Success() throws Exception {
        when(abonnementRepository.findByUserIdAndCompetitionId(userId, competitionId))
                .thenReturn(Optional.of(abonnement));

        mockMvc.perform(delete("/api/abonnements/unsubscribe")
                .param("userId", userId.toString())
                .param("competitionId", competitionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Désabonnement réussi"))
                .andExpect(jsonPath("$.competitionId").value(competitionId));
    }

    @Test
    void testDesabonnerCompetition_MissingUserId() throws Exception {
        mockMvc.perform(delete("/api/abonnements/unsubscribe")
                .param("competitionId", competitionId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDesabonnerCompetition_MissingCompetitionId() throws Exception {
        mockMvc.perform(delete("/api/abonnements/unsubscribe")
                .param("userId", userId.toString()))
                .andExpect(status().isBadRequest());
    }
}