package com.ciblorgasport.geolocationservice.service;

import com.ciblorgasport.geolocationservice.dto.PositionRequest;
import com.ciblorgasport.geolocationservice.dto.PositionResponse;
import com.ciblorgasport.geolocationservice.dto.WebSocketPositionMessage;
import com.ciblorgasport.geolocationservice.entity.AthletePosition;
import com.ciblorgasport.geolocationservice.exception.ResourceNotFoundException;
import com.ciblorgasport.geolocationservice.repository.AthletePositionRepository;
import com.ciblorgasport.geolocationservice.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AthletePositionService {

    private static final Logger logger = LoggerFactory.getLogger(AthletePositionService.class);

    private final AthletePositionRepository positionRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtils jwtUtils;

    public AthletePositionService(AthletePositionRepository positionRepository,
                                  SimpMessagingTemplate messagingTemplate,
                                  JwtUtils jwtUtils) {
        this.positionRepository = positionRepository;
        this.messagingTemplate = messagingTemplate;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public PositionResponse recordPosition(Long athleteId, PositionRequest request, String token) {
        Long tokenUserId = jwtUtils.getUserIdFromJwtToken(token);
        if (!tokenUserId.equals(athleteId)) {
            logger.warn("Accès refusé : userId={} essaie d'enregistrer pour athleteId={}", tokenUserId, athleteId);
            throw new AccessDeniedException("Un athlète ne peut enregistrer que sa propre position");
        }

        AthletePosition position = new AthletePosition(
                athleteId,
                request.getLatitude(),
                request.getLongitude(),
                LocalDateTime.now()
        );
        positionRepository.save(position);

        WebSocketPositionMessage message = new WebSocketPositionMessage(
                athleteId,
                position.getLatitude(),
                position.getLongitude(),
                position.getTimestamp()
        );
        messagingTemplate.convertAndSend("/topic/athletes/" + athleteId, message);
        logger.info("Position enregistrée et diffusée sur /topic/athletes/{}", athleteId);

        return toResponse(position);
    }

    @Transactional(readOnly = true)
    public PositionResponse getLastPosition(Long athleteId) {
        AthletePosition position = positionRepository.findTopByAthleteIdOrderByTimestampDesc(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune position trouvée pour l'athlète id=" + athleteId));
        return toResponse(position);
    }

    @Transactional(readOnly = true)
    public List<PositionResponse> getHistory(Long athleteId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        return positionRepository.findByAthleteIdAndTimestampBetweenOrderByTimestampAsc(athleteId, dateDebut, dateFin)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePositions(Long athleteId) {
        positionRepository.deleteAllByAthleteId(athleteId);
        logger.info("Positions supprimées pour athleteId={}", athleteId);
    }

    private PositionResponse toResponse(AthletePosition p) {
        return new PositionResponse(p.getId(), p.getAthleteId(), p.getLatitude(), p.getLongitude(), p.getTimestamp());
    }
}
