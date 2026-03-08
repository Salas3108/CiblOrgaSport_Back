package com.ciblorgasport.eventservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class LieuServiceClient {
    @Value("${lieu-service.url:http://localhost:8089}")
    private String lieuServiceUrl;

    public boolean existsById(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String url = lieuServiceUrl + "/lieux/" + id;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getStatusCode().is2xxSuccessful() && response.getBody() != null;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
