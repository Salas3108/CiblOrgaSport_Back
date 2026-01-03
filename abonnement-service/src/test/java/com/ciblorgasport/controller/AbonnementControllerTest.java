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
    private UUID competitionId;
    private Abonnement abonnement;

    @BeforeEach
    void setUp() {
        userId = 1L;
        competitionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        abonnement = new Abonnement(userId, competitionId);
        abonnement.setId(UUID.randomUUID());
    }

    @Test
    void testGetMesAbonnements() throws Exception {
        // Given
        List<Abonnement> abonnements = Arrays.asList(abonnement);
        when(abonnementRepository.findByUserId(userId)).thenReturn(abonnements);

        // When & Then
        mockMvc.perform(get("/api/abonnements/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].competitionId").value(competitionId.toString()));
    }

    @Test
    void testGetMesAbonnements_Empty() throws Exception {
        // Given
        when(abonnementRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/abonnements/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testSabonnerCompetition_Success() throws Exception {
        // Given
        when(abonnementRepository.existsByUserIdAndCompetitionId(userId, competitionId)).thenReturn(false);
        when(abonnementRepository.save(any(Abonnement.class))).thenReturn(abonnement);

        // When & Then
        mockMvc.perform(post("/api/abonnements/subscribe")
                .param("userId", userId.toString())
                .param("competitionId", competitionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Abonnement réussi"))
                .andExpect(jsonPath("$.competitionId").value(competitionId.toString()));
    }

    @Test
    void testSabonnerCompetition_AlreadySubscribed() throws Exception {
        // Given
        when(abonnementRepository.existsByUserIdAndCompetitionId(userId, competitionId)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/abonnements/subscribe")
                .param("userId", userId.toString())
                .param("competitionId", competitionId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Déjà abonné à cette compétition"));
    }

    @Test
    void testSabonnerCompetition_MissingUserId() throws Exception {
        // When & Then
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
    void testSabonnerCompetition_InvalidUUID() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/abonnements/subscribe")
                .param("userId", userId.toString())
                .param("competitionId", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDesabonnerCompetition_Success() throws Exception {
        // Given
        when(abonnementRepository.findByUserIdAndCompetitionId(userId, competitionId))
                .thenReturn(Optional.of(abonnement));

        // When & Then
        mockMvc.perform(delete("/api/abonnements/unsubscribe")
                .param("userId", userId.toString())
                .param("competitionId", competitionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Désabonnement réussi"))
                .andExpect(jsonPath("$.competitionId").value(competitionId.toString()));
    }

    @Test
    void testDesabonnerCompetition_MissingUserId() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/abonnements/unsubscribe")
                .param("competitionId", competitionId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDesabonnerCompetition_MissingCompetitionId() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/abonnements/unsubscribe")
                .param("userId", userId.toString()))
                .andExpect(status().isBadRequest());
    }
}