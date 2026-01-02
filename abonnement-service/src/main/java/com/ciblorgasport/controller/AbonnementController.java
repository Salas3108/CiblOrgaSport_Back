package com.ciblorgasport.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ciblorgasport.entity.Abonnement;
import com.ciblorgasport.repository.AbonnementRepository;

@RestController
@RequestMapping("/api/abonnements")
public class AbonnementController {

    private final AbonnementRepository abonnementRepo;
    private final RestTemplate restTemplate;

    @Autowired
    public AbonnementController(AbonnementRepository abonnementRepo, RestTemplate restTemplate) {
        this.abonnementRepo = abonnementRepo;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMesAbonnements(@PathVariable Long userId) {
        List<Abonnement> abonnements = abonnementRepo.findByUserId(userId);
        return ResponseEntity.ok(abonnements);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> sabonnerCompetition(@RequestParam Long userId, @RequestParam UUID competitionId) {
        if (abonnementRepo.existsByUserIdAndCompetitionId(userId, competitionId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Déjà abonné à cette compétition"));
        }
        // Optionnel : vérifier l'existence de la compétition via REST si besoin
        Abonnement abonnement = new Abonnement(userId, competitionId);
        abonnementRepo.save(abonnement);
        return ResponseEntity.ok(Map.of("message", "Abonnement réussi", "competitionId", competitionId));
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<?> desabonnerCompetition(@RequestParam Long userId, @RequestParam UUID competitionId) {
        Abonnement abonnement = abonnementRepo.findByUserIdAndCompetitionId(userId, competitionId)
            .orElseThrow(() -> new RuntimeException("Non abonné à cette compétition"));
        abonnementRepo.delete(abonnement);
        return ResponseEntity.ok(Map.of("message", "Désabonnement réussi", "competitionId", competitionId));
    }
}
