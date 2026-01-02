package com.ciblorgasport;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ciblorgasport.repository.AbonnementRepository;

@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
public class AbonnementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AbonnementRepository abonnementRepository;

    @Test
    public void testSAbonnerCompetition() throws Exception {
        Long userId = 1L;
        UUID competitionId = UUID.randomUUID();
        mockMvc.perform(post("/api/abonnements/subscribe")
                .param("userId", userId.toString())
                .param("competitionId", competitionId.toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());
    }
}
