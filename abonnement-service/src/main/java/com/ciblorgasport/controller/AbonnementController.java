package com.ciblorgasport.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.ciblorgasport.dto.AbonnementDTO;
import com.ciblorgasport.dto.AbonnementMapper;

@RestController
@RequestMapping("/api/abonnements")
public class AbonnementController {

    private final AbonnementRepository abonnementRepo;
    private final RestTemplate restTemplate;
    private final AbonnementMapper abonnementMapper;

    @Autowired
    public AbonnementController(AbonnementRepository abonnementRepo, RestTemplate restTemplate, AbonnementMapper abonnementMapper) {
        this.abonnementRepo = abonnementRepo;
        this.restTemplate = restTemplate;
        this.abonnementMapper = abonnementMapper;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMesAbonnements(@PathVariable Long userId) {
        List<Abonnement> abonnements = abonnementRepo.findByUserId(userId);
        List<AbonnementDTO> dtos = abonnements.stream().map(abonnementMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> sabonnerCompetition(@RequestParam Long userId, @RequestParam UUID competitionId) {
        if (abonnementRepo.existsByUserIdAndCompetitionId(userId, competitionId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Déjà abonné à cette compétition"));
        }
        Abonnement abonnement = new Abonnement(userId, competitionId);
        Abonnement saved = abonnementRepo.save(abonnement);
        return ResponseEntity.ok(abonnementMapper.toDto(saved));
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<?> desabonnerCompetition(@RequestParam Long userId, @RequestParam UUID competitionId) {
        Abonnement abonnement = abonnementRepo.findByUserIdAndCompetitionId(userId, competitionId)
            .orElseThrow(() -> new RuntimeException("Non abonné à cette compétition"));
        AbonnementDTO dto = abonnementMapper.toDto(abonnement);
        abonnementRepo.delete(abonnement);
        return ResponseEntity.ok(dto);
    }
}
