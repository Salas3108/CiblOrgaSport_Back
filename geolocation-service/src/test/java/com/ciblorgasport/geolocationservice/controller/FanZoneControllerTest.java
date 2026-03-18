package com.ciblorgasport.geolocationservice.controller;

import com.ciblorgasport.geolocationservice.config.SecurityConfig;
import com.ciblorgasport.geolocationservice.dto.FanZoneRequest;
import com.ciblorgasport.geolocationservice.dto.FanZoneResponse;
import com.ciblorgasport.geolocationservice.exception.GlobalExceptionHandler;
import com.ciblorgasport.geolocationservice.exception.ResourceNotFoundException;
import com.ciblorgasport.geolocationservice.security.AuthTokenFilter;
import com.ciblorgasport.geolocationservice.security.JwtUtils;
import com.ciblorgasport.geolocationservice.service.FanZoneBusinessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FanZoneController.class)
@Import({SecurityConfig.class, AuthTokenFilter.class, GlobalExceptionHandler.class})
class FanZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FanZoneBusinessService fanZoneService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    // ---- POST /api/geo/fanzones ----

    @Test
    @WithMockUser(roles = "ADMIN")
    void createFanZone_admin_201() throws Exception {
        FanZoneRequest req = new FanZoneRequest();
        req.setNom("Zone Olympique");
        req.setLatitude(48.8566);
        req.setLongitude(2.3522);

        FanZoneResponse resp = new FanZoneResponse();
        resp.setId(1L);
        resp.setNom("Zone Olympique");
        when(fanZoneService.create(any())).thenReturn(resp);

        mockMvc.perform(post("/api/geo/fanzones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Zone Olympique"));
    }

    @Test
    @WithMockUser(roles = "COMMISSAIRE")
    void createFanZone_nonAdmin_403() throws Exception {
        FanZoneRequest req = new FanZoneRequest();
        req.setNom("Zone X");
        req.setLatitude(48.8566);
        req.setLongitude(2.3522);

        mockMvc.perform(post("/api/geo/fanzones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createFanZone_invalidBody_400() throws Exception {
        // nom manquant
        mockMvc.perform(post("/api/geo/fanzones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"latitude\": 48.8566, \"longitude\": 2.3522}"))
                .andExpect(status().isBadRequest());
    }

    // ---- GET /api/geo/fanzones ----

    @Test
    void getAllFanZones_public_200() throws Exception {
        FanZoneResponse r1 = new FanZoneResponse();
        r1.setId(1L);
        r1.setNom("Zone A");
        FanZoneResponse r2 = new FanZoneResponse();
        r2.setId(2L);
        r2.setNom("Zone B");
        when(fanZoneService.findAll()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/geo/fanzones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ---- GET /api/geo/fanzones/nearby ----

    @Test
    void getNearbyFanZones_public_200() throws Exception {
        FanZoneResponse nearby = new FanZoneResponse();
        nearby.setId(1L);
        nearby.setNom("Zone Proche");
        nearby.setDistance(120.5);
        when(fanZoneService.findNearby(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of(nearby));

        mockMvc.perform(get("/api/geo/fanzones/nearby")
                        .param("lat", "48.8566")
                        .param("lng", "2.3522")
                        .param("rayon", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Zone Proche"))
                .andExpect(jsonPath("$[0].distance").value(120.5));
    }

    @Test
    void getNearbyFanZones_defaultRayon_200() throws Exception {
        when(fanZoneService.findNearby(anyDouble(), anyDouble(), eq(500.0))).thenReturn(List.of());

        mockMvc.perform(get("/api/geo/fanzones/nearby")
                        .param("lat", "48.8566")
                        .param("lng", "2.3522"))
                .andExpect(status().isOk());
    }

    // ---- DELETE /api/geo/fanzones/{id} ----

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFanZone_admin_204() throws Exception {
        doNothing().when(fanZoneService).delete(1L);

        mockMvc.perform(delete("/api/geo/fanzones/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFanZone_notFound_404() throws Exception {
        doThrow(new ResourceNotFoundException("Fan zone introuvable id=99"))
                .when(fanZoneService).delete(99L);

        mockMvc.perform(delete("/api/geo/fanzones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "ATHLETE")
    void deleteFanZone_nonAdmin_403() throws Exception {
        mockMvc.perform(delete("/api/geo/fanzones/1"))
                .andExpect(status().isForbidden());
    }
}
