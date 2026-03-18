package com.ciblorgasport.geolocationservice.controller;

import com.ciblorgasport.geolocationservice.config.SecurityConfig;
import com.ciblorgasport.geolocationservice.dto.PositionRequest;
import com.ciblorgasport.geolocationservice.dto.PositionResponse;
import com.ciblorgasport.geolocationservice.exception.GlobalExceptionHandler;
import com.ciblorgasport.geolocationservice.exception.ResourceNotFoundException;
import com.ciblorgasport.geolocationservice.security.AuthTokenFilter;
import com.ciblorgasport.geolocationservice.security.JwtUtils;
import com.ciblorgasport.geolocationservice.service.AthletePositionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AthletePositionController.class)
@Import({SecurityConfig.class, AuthTokenFilter.class, GlobalExceptionHandler.class})
class AthletePositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AthletePositionService positionService;

    @MockBean
    private JwtUtils jwtUtils;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ---- POST /api/geo/athletes/{athleteId}/position ----

    @Test
    @WithMockUser(roles = "ATHLETE")
    void postPosition_athlete_200() throws Exception {
        PositionRequest req = new PositionRequest();
        req.setLatitude(48.8566);
        req.setLongitude(2.3522);

        PositionResponse resp = new PositionResponse(1L, 1L, 48.8566, 2.3522, LocalDateTime.now());
        when(jwtUtils.validateJwtToken(any())).thenReturn(false);
        when(positionService.recordPosition(eq(1L), any(), any())).thenReturn(resp);

        mockMvc.perform(post("/api/geo/athletes/1/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test-token")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.athleteId").value(1))
                .andExpect(jsonPath("$.latitude").value(48.8566));
    }

    @Test
    @WithMockUser(roles = "COMMISSAIRE")
    void postPosition_commissaire_403() throws Exception {
        PositionRequest req = new PositionRequest();
        req.setLatitude(48.8566);
        req.setLongitude(2.3522);

        mockMvc.perform(post("/api/geo/athletes/1/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test-token")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ATHLETE")
    void postPosition_invalidBody_400() throws Exception {
        mockMvc.perform(post("/api/geo/athletes/1/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test-token")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ---- GET /api/geo/athletes/{athleteId}/position ----

    @Test
    @WithMockUser(roles = "COMMISSAIRE")
    void getLastPosition_commissaire_200() throws Exception {
        PositionResponse resp = new PositionResponse(1L, 1L, 48.8566, 2.3522, LocalDateTime.now());
        when(positionService.getLastPosition(1L)).thenReturn(resp);

        mockMvc.perform(get("/api/geo/athletes/1/position"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.athleteId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLastPosition_admin_200() throws Exception {
        PositionResponse resp = new PositionResponse(1L, 1L, 48.0, 2.0, LocalDateTime.now());
        when(positionService.getLastPosition(1L)).thenReturn(resp);

        mockMvc.perform(get("/api/geo/athletes/1/position"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ATHLETE")
    void getLastPosition_athlete_403() throws Exception {
        mockMvc.perform(get("/api/geo/athletes/1/position"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "COMMISSAIRE")
    void getLastPosition_notFound_404() throws Exception {
        when(positionService.getLastPosition(99L))
                .thenThrow(new ResourceNotFoundException("Aucune position"));

        mockMvc.perform(get("/api/geo/athletes/99/position"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ---- GET /api/geo/athletes/{athleteId}/history ----

    @Test
    @WithMockUser(roles = "COMMISSAIRE")
    void getHistory_200() throws Exception {
        when(positionService.getHistory(any(), any(), any())).thenReturn(List.of(
                new PositionResponse(1L, 1L, 48.0, 2.0, LocalDateTime.now())
        ));

        mockMvc.perform(get("/api/geo/athletes/1/history")
                        .param("dateDebut", "2026-07-15T10:00:00")
                        .param("dateFin", "2026-07-15T12:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ---- DELETE /api/geo/athletes/{athleteId}/positions ----

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePositions_admin_204() throws Exception {
        doNothing().when(positionService).deletePositions(1L);

        mockMvc.perform(delete("/api/geo/athletes/1/positions"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "COMMISSAIRE")
    void deletePositions_commissaire_403() throws Exception {
        mockMvc.perform(delete("/api/geo/athletes/1/positions"))
                .andExpect(status().isForbidden());
    }
}
