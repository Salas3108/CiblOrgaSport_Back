package com.ciblorgasport.resultatsservice.client;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EventServiceClient {

    private static final Logger log = LoggerFactory.getLogger(EventServiceClient.class);

    @Value("${event-service.url:http://localhost:8084}")
    private String eventServiceUrl;

    public Optional<EpreuveSummary> getEpreuveSummary(Long epreuveId) {
        if (epreuveId == null) {
            return Optional.empty();
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = eventServiceUrl + "/api/epreuves/" + epreuveId;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Optional.empty();
            }

            Map<?, ?> body = response.getBody();
            String nom = body.get("nom") != null ? String.valueOf(body.get("nom")) : null;

            Long competitionId = null;
            Object competitionObj = body.get("competitionId");
            if (competitionObj instanceof Number number) {
                competitionId = number.longValue();
            } else if (competitionObj instanceof String str && !str.isBlank()) {
                competitionId = Long.parseLong(str);
            }

            return Optional.of(new EpreuveSummary(nom, competitionId));
        } catch (Exception ex) {
            log.warn("Unable to fetch epreuve summary for epreuveId={}: {}", epreuveId, ex.getMessage());
            return Optional.empty();
        }
    }

    public static class EpreuveSummary {
        private final String nom;
        private final Long competitionId;

        public EpreuveSummary(String nom, Long competitionId) {
            this.nom = nom;
            this.competitionId = competitionId;
        }

        public String getNom() {
            return nom;
        }

        public Long getCompetitionId() {
            return competitionId;
        }
    }
}
package com.ciblorgasport.resultatsservice.client;

import com.ciblorgasport.resultatsservice.client.dto.EpreuveContextDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

/**
 * Client RestTemplate vers l'event-service.
 * Retourne null en cas d'erreur (dégradation gracieuse).
 */
@Component
public class EventServiceClient {

    @Value("${event-service.url:http://localhost:8084}")
    private String eventServiceUrl;

    /**
     * Récupère le contexte complet d'une épreuve (discipline incluse).
     * Appelle GET /epreuves/{id} puis GET /competitions/{competitionId} si nécessaire.
     */
    public EpreuveContextDto getEpreuveContext(Long epreuveId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Void> entity = new HttpEntity<>(buildAuthHeaders());

            ResponseEntity<Map> response = restTemplate.exchange(
                    eventServiceUrl + "/epreuves/" + epreuveId,
                    HttpMethod.GET, entity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return null;
            }

            Map<String, Object> body = response.getBody();
            EpreuveContextDto ctx = new EpreuveContextDto();
            ctx.setId(toLong(body.get("id")));
            ctx.setNom((String) body.get("nom"));
            ctx.setTypeEpreuve((String) body.get("typeEpreuve"));
            ctx.setNiveauEpreuve((String) body.get("niveauEpreuve"));
            ctx.setAthleteIds(toLongSet(body.get("athleteIds")));
            ctx.setEquipeIds(toLongSet(body.get("equipeIds")));

            // Récupérer la discipline depuis la competition
            String discipline = extractDiscipline(body, restTemplate, entity);
            ctx.setDiscipline(discipline);

            return ctx;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private String extractDiscipline(Map<String, Object> epreuveBody,
                                     RestTemplate restTemplate,
                                     HttpEntity<Void> entity) {
        // Cas 1 : competition est sérialisée en objet inline
        Object competitionObj = epreuveBody.get("competition");
        if (competitionObj instanceof Map<?, ?> compMap) {
            Object disc = ((Map<String, Object>) compMap).get("discipline");
            if (disc != null) return disc.toString();
        }

        // Cas 2 : seul competitionId est disponible → second appel
        Long competitionId = toLong(epreuveBody.get("competitionId"));
        if (competitionId == null) return null;

        try {
            ResponseEntity<Map> compResponse = restTemplate.exchange(
                    eventServiceUrl + "/competitions/" + competitionId,
                    HttpMethod.GET, entity, Map.class);
            if (compResponse.getStatusCode().is2xxSuccessful() && compResponse.getBody() != null) {
                Object disc = compResponse.getBody().get("discipline");
                return disc != null ? disc.toString() : null;
            }
        } catch (Exception ignored) {
            // discipline non disponible, ClassementService logguera l'erreur
        }
        return null;
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

    @SuppressWarnings("unchecked")
    private Set<Long> toLongSet(Object value) {
        Set<Long> result = new HashSet<>();
        if (!(value instanceof Collection<?> col)) return result;
        for (Object item : col) {
            Long id = toLong(item);
            if (id != null) result.add(id);
        }
        return result;
    }
}
