package com.ciblorgasport.geolocationservice.service;

import com.ciblorgasport.geolocationservice.dto.PositionRequest;
import com.ciblorgasport.geolocationservice.dto.PositionResponse;
import com.ciblorgasport.geolocationservice.dto.WebSocketPositionMessage;
import com.ciblorgasport.geolocationservice.entity.AthleteGeoConfig;
import com.ciblorgasport.geolocationservice.entity.AthletePosition;
import com.ciblorgasport.geolocationservice.exception.GeolocDisabledException;
import com.ciblorgasport.geolocationservice.exception.ResourceNotFoundException;
import com.ciblorgasport.geolocationservice.repository.AthleteGeoConfigRepository;
import com.ciblorgasport.geolocationservice.repository.AthletePositionRepository;
import com.ciblorgasport.geolocationservice.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AthletePositionService {

    private static final Logger logger = LoggerFactory.getLogger(AthletePositionService.class);

    private final AthletePositionRepository positionRepository;
    private final AthleteGeoConfigRepository geoConfigRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtils jwtUtils;

    public AthletePositionService(AthletePositionRepository positionRepository,
                                  AthleteGeoConfigRepository geoConfigRepository,
                                  SimpMessagingTemplate messagingTemplate,
                                  JwtUtils jwtUtils) {
        this.positionRepository = positionRepository;
        this.geoConfigRepository = geoConfigRepository;
        this.messagingTemplate = messagingTemplate;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public PositionResponse recordPosition(Long athleteId, PositionRequest request, String token) {
        Long tokenUserId = jwtUtils.getUserIdFromJwtToken(token);
        if (!tokenUserId.equals(athleteId)) {
            logger.warn("Tentative d'enregistrement de position refusée : userId={} essaie d'enregistrer pour athleteId={}", tokenUserId, athleteId);
            throw new org.springframework.security.access.AccessDeniedException(
                    "Un athlète ne peut enregistrer que sa propre position");
        }

        AthleteGeoConfig config = geoConfigRepository.findById(athleteId)
                .orElseThrow(() -> {
                    logger.warn("Tentative d'enregistrement de position refusée : aucune config pour athleteId={}", athleteId);
                    return new GeolocDisabledException("La géolocalisation n'est pas activée pour cet athlète (id=" + athleteId + ")");
                });

        if (!config.isGeolocActive()) {
            logger.warn("Tentative d'enregistrement de position refusée : geolocActive=false pour athleteId={}", athleteId);
            throw new GeolocDisabledException("La géolocalisation est désactivée pour cet athlète (id=" + athleteId + ")");
        }

        AthletePosition position = new AthletePosition(
                athleteId,
                request.getLatitude(),
                request.getLongitude(),
                LocalDateTime.now(),
                request.getEpreuveId()
        );
        positionRepository.save(position);

        WebSocketPositionMessage message = new WebSocketPositionMessage(
                athleteId,
                config.getNom(),
                position.getLatitude(),
                position.getLongitude(),
                position.getTimestamp(),
                position.getEpreuveId()
        );
        messagingTemplate.convertAndSend("/topic/athletes/" + position.getEpreuveId(), message);
        logger.info("Position enregistrée et diffusée pour athleteId={} epreuveId={}", athleteId, position.getEpreuveId());

        return toResponse(position);
    }

    @Transactional(readOnly = true)
    public PositionResponse getLastPosition(Long athleteId) {
        AthletePosition position = positionRepository.findTopByAthleteIdOrderByTimestampDesc(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune position trouvée pour l'athlète id=" + athleteId));
        return toResponse(position);
    }

    @Transactional(readOnly = true)
    public List<PositionResponse> getEpreuvePositions(Long epreuveId) {
        return positionRepository.findLatestPositionsPerAthleteByEpreuveId(epreuveId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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

    @Transactional
    public AthleteGeoConfig upsertConfig(Long athleteId, boolean geolocActive, String nom) {
        AthleteGeoConfig config = geoConfigRepository.findById(athleteId)
                .orElse(new AthleteGeoConfig(athleteId, geolocActive, nom));
        config.setGeolocActive(geolocActive);
        if (nom != null) {
            config.setNom(nom);
        }
        return geoConfigRepository.save(config);
    }

    private PositionResponse toResponse(AthletePosition p) {
        return new PositionResponse(p.getId(), p.getAthleteId(), p.getLatitude(), p.getLongitude(), p.getTimestamp(), p.getEpreuveId());
    }
}
