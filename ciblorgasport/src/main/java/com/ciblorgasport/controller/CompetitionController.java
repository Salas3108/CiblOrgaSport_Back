package com.ciblorgasport.controller;

import com.ciblorgasport.entity.*;
import com.ciblorgasport.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CompetitionController {

    private final CompetitionRepository competitionRepo;
    private final RestTemplate restTemplate;

    @Autowired
    public CompetitionController(CompetitionRepository competitionRepo, RestTemplate restTemplate) {
        this.competitionRepo = competitionRepo;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/competitions")
    public ResponseEntity<?> getAllCompetitions(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        List<Competition> competitions = competitionRepo.findAllWithDetails();

        List<Map<String, Object>> competitionsWithAbonnement = competitions.stream()
            .map(competition -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", competition.getId());
                map.put("name", competition.getName());
                map.put("event", competition.getEvent());
                map.put("epreuves", competition.getEpreuves());

                boolean estAbonne = abonnementRepo.existsByUserIdAndCompetitionId(
                    userId, competition.getId());
                map.put("estAbonne", estAbonne);

                long nombreAbonnes = abonnementRepo.countByCompetitionId(competition.getId());
                map.put("nombreAbonnes", nombreAbonnes);
                
                return map;
            })
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(competitionsWithAbonnement);
    }
    
    @PostMapping("/competitions/{competitionId}/sabonner")
    public ResponseEntity<?> sabonnerCompetition(
            @PathVariable UUID competitionId,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        // Appel au microservice abonnement-service
        String url = "http://localhost:8082/api/abonnements/subscribe?userId=" + userId + "&competitionId=" + competitionId;
        try {
            restTemplate.postForEntity(url, null, String.class);
            return ResponseEntity.ok(Map.of(
                "message", "Abonnement réussi à la compétition",
                "competitionId", competitionId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de l'abonnement"));
        }
    }
    
    @DeleteMapping("/competitions/{competitionId}/desabonner")
    public ResponseEntity<?> desabonnerCompetition(
            @PathVariable UUID competitionId,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        // Appel au microservice abonnement-service
        String url = "http://localhost:8082/api/abonnements/unsubscribe?userId=" + userId + "&competitionId=" + competitionId;
        try {
            restTemplate.delete(url);
            return ResponseEntity.ok(Map.of(
                "message", "Désabonnement réussi",
                "competitionId", competitionId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors du désabonnement"));
        }
    }
    
    @GetMapping("/mes-abonnements")
    public ResponseEntity<?> getMesAbonnements(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<Abonnement> abonnements = abonnementRepo.findByUserId(userId);
        
        return ResponseEntity.ok(abonnements);
    }
    
   
    private Long getCurrentUserId(Authentication authentication) {
        return 1L;
    }
}