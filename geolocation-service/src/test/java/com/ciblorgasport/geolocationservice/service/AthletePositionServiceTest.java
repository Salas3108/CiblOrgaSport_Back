package com.ciblorgasport.geolocationservice.service;

import com.ciblorgasport.geolocationservice.dto.PositionRequest;
import com.ciblorgasport.geolocationservice.dto.PositionResponse;
import com.ciblorgasport.geolocationservice.entity.AthletePosition;
import com.ciblorgasport.geolocationservice.exception.ResourceNotFoundException;
import com.ciblorgasport.geolocationservice.repository.AthletePositionRepository;
import com.ciblorgasport.geolocationservice.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AthletePositionServiceTest {

    @Mock
    private AthletePositionRepository positionRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AthletePositionService service;

    private PositionRequest request;

    @BeforeEach
    void setUp() {
        request = new PositionRequest();
        request.setLatitude(48.8566);
        request.setLongitude(2.3522);
    }

    @Test
    void recordPosition_nominalCase_savesAndBroadcasts() {
        when(jwtUtils.getUserIdFromJwtToken("token")).thenReturn(1L);

        AthletePosition saved = new AthletePosition(1L, 48.8566, 2.3522, LocalDateTime.now());
        saved.setId(10L);
        when(positionRepository.save(any())).thenReturn(saved);

        PositionResponse response = service.recordPosition(1L, request, "token");

        assertThat(response.getAthleteId()).isEqualTo(1L);
        assertThat(response.getLatitude()).isEqualTo(48.8566);
        verify(positionRepository).save(any(AthletePosition.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/athletes/1"), any(Object.class));
    }

    @Test
    void recordPosition_wrongUserId_throwsAccessDenied() {
        when(jwtUtils.getUserIdFromJwtToken("token")).thenReturn(99L);

        assertThatThrownBy(() -> service.recordPosition(1L, request, "token"))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(positionRepository, messagingTemplate);
    }

    @Test
    void getLastPosition_existingAthlete_returnsPosition() {
        AthletePosition pos = new AthletePosition(1L, 48.0, 2.0, LocalDateTime.now());
        pos.setId(5L);
        when(positionRepository.findTopByAthleteIdOrderByTimestampDesc(1L)).thenReturn(Optional.of(pos));

        PositionResponse response = service.getLastPosition(1L);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getAthleteId()).isEqualTo(1L);
    }

    @Test
    void getLastPosition_noPosition_throwsNotFound() {
        when(positionRepository.findTopByAthleteIdOrderByTimestampDesc(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLastPosition(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getHistory_returnsPositionsInRange() {
        LocalDateTime debut = LocalDateTime.now().minusHours(2);
        LocalDateTime fin = LocalDateTime.now();
        List<AthletePosition> positions = List.of(
                new AthletePosition(1L, 48.0, 2.0, debut.plusMinutes(30))
        );
        when(positionRepository.findByAthleteIdAndTimestampBetweenOrderByTimestampAsc(1L, debut, fin))
                .thenReturn(positions);

        List<PositionResponse> result = service.getHistory(1L, debut, fin);

        assertThat(result).hasSize(1);
    }

    @Test
    void deletePositions_callsRepository() {
        service.deletePositions(1L);

        verify(positionRepository).deleteAllByAthleteId(1L);
    }
}
