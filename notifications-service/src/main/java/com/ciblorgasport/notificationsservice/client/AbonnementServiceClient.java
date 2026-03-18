package com.ciblorgasport.notificationsservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * HTTP client to fetch subscription data from abonnement-service.
 */
@Component
public class AbonnementServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AbonnementServiceClient.class);

    private final RestTemplate restTemplate;
    private final String abonnementServiceUrl;

    public AbonnementServiceClient(
            RestTemplate restTemplate,
            @Value("${abonnement-service.url}") String abonnementServiceUrl) {
        this.restTemplate = restTemplate;
        this.abonnementServiceUrl = abonnementServiceUrl;
    }

    /**
     * Returns the list of userIds subscribed to the given competition with
     * notifications enabled.
     *
     * @param competitionId competition identifier (Long)
     * @return list of userIds, or empty list if the call fails
     */
    public List<Long> getSubscribersWithNotifications(Long competitionId) {
        try {
            String url = abonnementServiceUrl
                    + "/api/abonnements/internal/competition/"
                    + competitionId
                    + "/subscribers";
            ResponseEntity<List<Long>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Long>>() {}
            );
            List<Long> body = response.getBody();
            return body != null ? body : Collections.emptyList();
        } catch (RestClientException e) {
            log.warn("Failed to fetch subscribers from abonnement-service for competitionId={}: {}",
                    competitionId, e.getMessage());
            return Collections.emptyList();
        }
    }
}
