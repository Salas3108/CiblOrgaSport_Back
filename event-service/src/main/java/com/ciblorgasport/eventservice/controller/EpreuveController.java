package com.ciblorgasport.eventservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.eventservice.model.Epreuve;
import com.ciblorgasport.eventservice.model.Lieu;
import com.ciblorgasport.eventservice.repository.EpreuveRepository;
import com.ciblorgasport.eventservice.repository.LieuRepository;

@RestController
@RequestMapping({"/epreuves", "/api/epreuves"})
public class EpreuveController {
    @Autowired
    private EpreuveRepository epreuveRepository;
    @Autowired
    private LieuRepository lieuRepository;

    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Epreuve> getAllEpreuves() {
        return epreuveRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<Epreuve> createEpreuve(@RequestBody Epreuve epreuve) {
        Long lieuId = epreuve.getLieuId();
        if (lieuId == null && epreuve.getLieu() != null) {
            lieuId = epreuve.getLieu().getId();
        }
        if (lieuId != null) {
            Lieu lieu = lieuRepository.findById(lieuId).orElseThrow(() -> new RuntimeException("Lieu not found"));
            epreuve.setLieu(lieu);
        }
        Epreuve saved = epreuveRepository.save(epreuve);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Epreuve> getEpreuveById(@PathVariable Long id) {
        return epreuveRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<Epreuve> updateEpreuve(@PathVariable Long id, @RequestBody Epreuve epreuveDetails) {
        return epreuveRepository.findById(id)
                .map(existing -> {
                    existing.setNom(epreuveDetails.getNom());
                    existing.setDescription(epreuveDetails.getDescription());
                    existing.setDate(epreuveDetails.getDate());
                    existing.setHeureDebut(epreuveDetails.getHeureDebut());
                    existing.setHeureFin(epreuveDetails.getHeureFin());
                    Long lieuId = epreuveDetails.getLieuId();
                    if (lieuId == null && epreuveDetails.getLieu() != null) {
                        lieuId = epreuveDetails.getLieu().getId();
                    }
                    if (lieuId != null) {
                        Lieu lieu = lieuRepository.findById(lieuId).orElseThrow(() -> new RuntimeException("Lieu not found"));
                        existing.setLieu(lieu);
                    }
                    Epreuve updated = epreuveRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public void deleteEpreuve(@PathVariable Long id) {
        epreuveRepository.deleteById(id);
    }
}
