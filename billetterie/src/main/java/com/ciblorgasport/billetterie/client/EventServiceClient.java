package com.ciblorgasport.billetterie.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EventServiceClient {
    @Value("${event-service.url:http://localhost:8084}")
    private String eventServiceUrl;

    public boolean existsById(Long id) {
        if (id == null) return false;
        RestTemplate restTemplate = new RestTemplate();
        // Validate Epreuve existence (tickets reference epreuveId)
        // Use /api prefix to match event-service routing when present
        String url = eventServiceUrl + "/api/epreuves/" + id;
        try {
            System.out.println("[EventServiceClient] Checking epreuve existence at: " + url);
            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            System.out.println("[EventServiceClient] Response status: " + resp.getStatusCode());
            return resp.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println("[EventServiceClient] existsById error for URL=" + url + ": " + e.getMessage());
            return false;
        }
    }

    public java.util.Map fetchEventById(Long id) {
        if (id == null) return null;
        RestTemplate restTemplate = new RestTemplate();
        // Fetch Epreuve details by id to include in the ticket response
        // Use /api prefix to match event-service routing when present
        String url = eventServiceUrl + "/api/epreuves/" + id;
        try {
            System.out.println("[EventServiceClient] Fetch epreuve details at: " + url);
            ResponseEntity<java.util.Map> resp = restTemplate.getForEntity(url, java.util.Map.class);
            if (resp.getStatusCode().is2xxSuccessful()) return resp.getBody();
        } catch (Exception ignored) {}
        return null;
    }
}
