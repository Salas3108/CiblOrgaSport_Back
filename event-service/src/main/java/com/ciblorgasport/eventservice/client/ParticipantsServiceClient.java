package com.ciblorgasport.eventservice.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ciblorgasport.eventservice.dto.AthleteSexeDto;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class ParticipantsServiceClient {

    @Value("${participants-service.url:http://localhost:8087}")
    private String participantsServiceUrl;

    public boolean areValidAthletes(Collection<Long> athleteIds) {
        if (athleteIds == null || athleteIds.isEmpty()) {
            return true;
        }

        List<Long> validatedAthleteIds = fetchValidatedAthleteIds();
        Set<Long> validatedSet = new HashSet<>(validatedAthleteIds);
        Set<Long> requestedSet = normalizeToLongSet(athleteIds);
        return !requestedSet.isEmpty() && validatedSet.containsAll(requestedSet);
    }

    public boolean isValidEquipe(Long equipeId) {
        if (equipeId == null) {
            return true;
        }

        return areValidEquipes(List.of(equipeId));
    }

    public boolean areValidEquipes(Collection<Long> equipeIds) {
        if (equipeIds == null || equipeIds.isEmpty()) {
            return true;
        }

        List<Long> existingEquipeIds = fetchEquipeIds();
        Set<Long> existingSet = new HashSet<>(existingEquipeIds);
        Set<Long> requestedSet = normalizeToLongSet(equipeIds);
        return !requestedSet.isEmpty() && existingSet.containsAll(requestedSet);
    }

    private List<Long> fetchValidatedAthleteIds() {
        RestTemplate restTemplate = new RestTemplate();
        String url = participantsServiceUrl + "/api/commissaire/athletes/valides";

        HttpHeaders headers = new HttpHeaders();
        String authorization = resolveAuthorizationHeader();
        if (authorization != null && !authorization.isBlank()) {
            headers.set("Authorization", authorization);
        }

        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), List.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalStateException("participants-service returned an invalid response");
            }
            return extractIds(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new IllegalStateException(
                "Failed to validate athletes via participants-service: HTTP " + e.getStatusCode().value(),
                e
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to validate athletes via participants-service", e);
        }
    }

    private List<Long> fetchEquipeIds() {
        RestTemplate restTemplate = new RestTemplate();
        String url = participantsServiceUrl + "/api/commissaire/equipes";

        HttpHeaders headers = new HttpHeaders();
        String authorization = resolveAuthorizationHeader();
        if (authorization != null && !authorization.isBlank()) {
            headers.set("Authorization", authorization);
        }

        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), List.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalStateException("participants-service returned an invalid response");
            }
            return extractIds(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new IllegalStateException(
                "Failed to validate equipe via participants-service: HTTP " + e.getStatusCode().value(),
                e
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to validate equipe via participants-service", e);
        }
    }

    private List<Long> extractIds(List<?> payload) {
        List<Long> ids = new ArrayList<>();
        for (Object item : payload) {
            if (item instanceof Number numberItem) {
                ids.add(numberItem.longValue());
                continue;
            }
            if (item instanceof String strItem && !strItem.isBlank()) {
                try {
                    ids.add(Long.parseLong(strItem));
                    continue;
                } catch (NumberFormatException ignored) {
                    // Ignore malformed scalar values.
                }
            }
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }

            Object idObj = firstNonNull(
                map.get("id"),
                map.get("equipeId"),
                map.get("athleteId"),
                map.get("ID")
            );
            if (idObj instanceof Number number) {
                ids.add(number.longValue());
            } else if (idObj instanceof String str && !str.isBlank()) {
                try {
                    ids.add(Long.parseLong(str));
                } catch (NumberFormatException ignored) {
                    // Skip malformed ids from remote payload.
                }
            }
        }
        return ids;
    }

    private Object firstNonNull(Object... values) {
        for (Object value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private Set<Long> normalizeToLongSet(Collection<?> ids) {
        Set<Long> normalized = new HashSet<>();
        for (Object idObj : ids) {
            if (idObj instanceof Number number) {
                normalized.add(number.longValue());
            } else if (idObj instanceof String str && !str.isBlank()) {
                try {
                    normalized.add(Long.parseLong(str));
                } catch (NumberFormatException ignored) {
                    // Ignore malformed values.
                }
            }
        }
        return normalized;
    }

    public AthleteSexeDto getAthleteInfo(Long athleteId) {
        if (athleteId == null) {
            return null;
        }
        RestTemplate restTemplate = new RestTemplate();
        String url = participantsServiceUrl + "/internal/athletes/" + athleteId;
        try {
            return restTemplate.getForObject(url, AthleteSexeDto.class);
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de récupérer le profil de l'athlète " + athleteId + " via participants-service", e);
        }
    }

    public List<AthleteSexeDto> getEquipeAthletes(Long equipeId) {
        if (equipeId == null) {
            return List.of();
        }
        RestTemplate restTemplate = new RestTemplate();
        String url = participantsServiceUrl + "/internal/equipes/" + equipeId + "/athletes";
        try {
            AthleteSexeDto[] result = restTemplate.getForObject(url, AthleteSexeDto[].class);
            return result != null ? List.of(result) : List.of();
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de récupérer la composition de l'équipe " + equipeId + " via participants-service", e);
        }
    }

    private String resolveAuthorizationHeader() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        return request.getHeader("Authorization");
    }
}
