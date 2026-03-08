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
        return validatedSet.containsAll(new HashSet<>(athleteIds));
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
            return extractAthleteIds(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new IllegalStateException(
                "Failed to validate athletes via participants-service: HTTP " + e.getStatusCode().value(),
                e
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to validate athletes via participants-service", e);
        }
    }

    private List<Long> extractAthleteIds(List<?> athletesPayload) {
        List<Long> ids = new ArrayList<>();
        for (Object item : athletesPayload) {
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }

            Object idObj = map.get("id");
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

    private String resolveAuthorizationHeader() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        return request.getHeader("Authorization");
    }
}
