package com.ciblorgasport.controller;

import com.ciblorgasport.entity.*;
import com.ciblorgasport.repository.*;
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
    private final AbonnementRepository abonnementRepo;
    
    public CompetitionController(CompetitionRepository competitionRepo,
                                AbonnementRepository abonnementRepo) {
        this.competitionRepo = competitionRepo;
        this.abonnementRepo = abonnementRepo;
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
        
        if (abonnementRepo.existsByUserIdAndCompetitionId(userId, competitionId)) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Vous êtes déjà abonné à cette compétition"));
        }
        
        Competition competition = competitionRepo.findById(competitionId)
            .orElseThrow(() -> new RuntimeException("Compétition non trouvée"));
        
        Abonnement abonnement = new Abonnement(userId, competition);
        abonnementRepo.save(abonnement);
        
        return ResponseEntity.ok(Map.of(
            "message", "Abonnement réussi à la compétition: " + competition.getName(),
            "competitionId", competitionId,
            "competitionName", competition.getName()
        ));
    }
    
    @DeleteMapping("/competitions/{competitionId}/desabonner")
    public ResponseEntity<?> desabonnerCompetition(
            @PathVariable UUID competitionId,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        
        Abonnement abonnement = abonnementRepo.findByUserIdAndCompetitionId(userId, competitionId)
            .orElseThrow(() -> new RuntimeException("Vous n'êtes pas abonné à cette compétition"));
        
        abonnementRepo.delete(abonnement);
        
        return ResponseEntity.ok(Map.of(
            "message", "Désabonnement réussi",
            "competitionId", competitionId
        ));
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