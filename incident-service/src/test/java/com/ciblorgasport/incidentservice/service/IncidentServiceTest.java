package com.ciblorgasport.incidentservice.service;

import com.ciblorgasport.incidentservice.model.Incident;
import com.ciblorgasport.incidentservice.model.IncidentStatus;
import com.ciblorgasport.incidentservice.repository.IncidentRepository;
import com.ciblorgasport.incidentservice.service.impl.IncidentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnIncidentsForStatus() {
        Incident inc = new Incident();
        inc.setStatus(IncidentStatus.ACTIF);

        when(incidentRepository.findByStatus(IncidentStatus.ACTIF)).thenReturn(Collections.singletonList(inc));

        List<Incident> found = incidentService.findByStatus(IncidentStatus.ACTIF);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getStatus()).isEqualTo(IncidentStatus.ACTIF);
    }
}
