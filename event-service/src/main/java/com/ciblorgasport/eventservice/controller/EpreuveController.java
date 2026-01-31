package com.ciblorgasport.eventservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.ciblorgasport.eventservice.repository.EpreuveRepository;

@RestController
@RequestMapping({"/epreuves", "/api/epreuves"})
@PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")

public class EpreuveController {
    @Autowired
    private EpreuveRepository epreuveRepository;

    @GetMapping
    public List<Epreuve> getAllEpreuves() {
        return epreuveRepository.findAll();
    }

    @PostMapping
    public Epreuve createEpreuve(@RequestBody Epreuve epreuve) {
        return epreuveRepository.save(epreuve);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Epreuve> getEpreuveById(@PathVariable Long id) {
        return epreuveRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Epreuve updateEpreuve(@PathVariable Long id, @RequestBody Epreuve epreuveDetails) {
        Epreuve epreuve = epreuveRepository.findById(id).orElse(null);
        if (epreuve != null) {
            epreuve.setNom(epreuveDetails.getNom());
            epreuve.setDescription(epreuveDetails.getDescription());
            epreuve.setDate(epreuveDetails.getDate());
            epreuve.setHeureDebut(epreuveDetails.getHeureDebut());
            epreuve.setHeureFin(epreuveDetails.getHeureFin());
            return epreuveRepository.save(epreuve);
        }
        return null;
    }


    @DeleteMapping("/{id}")
    public void deleteEpreuve(@PathVariable Long id) {
        epreuveRepository.deleteById(id);
    }
}
