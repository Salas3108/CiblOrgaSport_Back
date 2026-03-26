package com.ciblorgasport.resultatsservice.client;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ciblorgasport.resultatsservice.client.dto.AthleteInfoDto;
import com.ciblorgasport.resultatsservice.client.dto.EquipeInfoDto;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Client RestTemplate vers le participants-service.
 * Retourne null en cas d'erreur (dégradation gracieuse).
 */
@Component
public class ParticipantsServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ParticipantsServiceClient.class);

    @Value("${participants-service.url:http://localhost:8087}")
    private String participantsServiceUrl;

    /**
     * Récupère les informations d'un athlète par son ID.
     * Endpoint : GET /commissaire/athletes/{id}/info
     */
    @SuppressWarnings("unchecked")
    public AthleteInfoDto getAthlete(Long athleteId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Void> entity = new HttpEntity<>(buildAuthHeaders());

            ResponseEntity<Map> response = restTemplate.exchange(
                    participantsServiceUrl + "/commissaire/athletes/" + athleteId + "/info",
                    HttpMethod.GET, entity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return null;
            }

            Map<String, Object> body = response.getBody();
            AthleteInfoDto dto = new AthleteInfoDto();
            dto.setId(toLong(body.get("id")));
            dto.setNom(toString(body.get("nom")));
            dto.setPrenom(toString(body.get("prenom")));
            dto.setPays(toString(body.get("pays")));
            return dto;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Récupère les informations d'une équipe par son ID.
     * Endpoint : GET /commissaire/equipes/{id}
     */
    @SuppressWarnings("unchecked")
    public EquipeInfoDto getEquipe(Long equipeId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Void> entity = new HttpEntity<>(buildAuthHeaders());

            ResponseEntity<Map> response = restTemplate.exchange(
                    participantsServiceUrl + "/commissaire/equipes/" + equipeId,
                    HttpMethod.GET, entity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return null;
            }

            Map<String, Object> body = response.getBody();
            EquipeInfoDto dto = new EquipeInfoDto();
            dto.setId(toLong(body.get("id")));
            dto.setNom(toString(body.get("nom")));
            dto.setPays(toString(body.get("pays")));
            return dto;
        } catch (Exception e) {
            return null;
        }
    }

    public Optional<String> getAthleteDisplayName(Long athleteId) {
        if (athleteId == null) {
            return Optional.empty();
        }

        String url = participantsServiceUrl + "/api/commissaire/athletes/" + athleteId + "/info";
        return executeNameLookup(url, true);
    }

    public Optional<String> getEquipeDisplayName(Long equipeId) {
        if (equipeId == null) {
            return Optional.empty();
        }

        String url = participantsServiceUrl + "/api/commissaire/equipes/" + equipeId;
        return executeNameLookup(url, false);
    }

    private Optional<String> executeNameLookup(String url, boolean athlete) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Void> entity = new HttpEntity<>(buildAuthHeaders());

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Optional.empty();
            }

            Map<?, ?> body = response.getBody();
            if (!athlete) {
                Object nomObj = body.get("nom");
                if (nomObj != null) {
                    return Optional.of(String.valueOf(nomObj));
                }
                return Optional.empty();
            }

            Object prenomObj = body.get("prenom");
            Object nomObj = body.get("nom");
            String prenom = prenomObj != null ? String.valueOf(prenomObj).trim() : "";
            String nom = nomObj != null ? String.valueOf(nomObj).trim() : "";
            String full = (prenom + " " + nom).trim();
            if (full.isBlank()) {
                return Optional.empty();
            }

            return Optional.of(full);
        } catch (Exception ex) {
            log.debug("Unable to resolve participant label from {}: {}", url, ex.getMessage());
            return Optional.empty();
        }
    }

    private HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = resolveAuthorizationHeader();
        if (auth != null && !auth.isBlank()) {
            headers.set("Authorization", auth);
        }
        return headers;
    }

    private String resolveAuthorizationHeader() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        return request.getHeader("Authorization");
    }

    private Long toLong(Object value) {
        if (value instanceof Number n) return n.longValue();
        if (value instanceof String s) {
            try { return Long.parseLong(s); } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }
}
