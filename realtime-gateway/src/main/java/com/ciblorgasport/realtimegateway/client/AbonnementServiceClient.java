package com.ciblorgasport.realtimegateway.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class AbonnementServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${abonnement-service.url:http://localhost:8085}")
    private String baseUrl;

    public List<Long> getSubscriberUserIds(UUID competitionId) {
        if (competitionId == null) return Collections.emptyList();
        String url = baseUrl + "/api/abonnements/competition/" + competitionId + "/userIds";
        ResponseEntity<List<Long>> resp = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return resp.getBody() == null ? Collections.emptyList() : resp.getBody();
    }
}
