package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Lieu;
import com.ciblorgasport.eventservice.repository.LieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lieux")
@PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
public class LieuController {
    @Autowired
    private LieuRepository lieuRepository;

    @GetMapping
    public List<Lieu> getAllLieux() {
        return lieuRepository.findAll();
    }

    @GetMapping("/{id}")
    public Lieu getLieuById(@PathVariable Long id) {
        return lieuRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Lieu createLieu(@RequestBody Lieu lieu) {
        return lieuRepository.save(lieu);
    }

    @PutMapping("/{id}")
    public Lieu updateLieu(@PathVariable Long id, @RequestBody Lieu lieuDetails) {
        Lieu lieu = lieuRepository.findById(id).orElse(null);
        if (lieu != null) {
            lieu.setNom(lieuDetails.getNom());
            lieu.setAdresse(lieuDetails.getAdresse());
            lieu.setVille(lieuDetails.getVille());
            lieu.setCodePostal(lieuDetails.getCodePostal());
            lieu.setPays(lieuDetails.getPays());
            return lieuRepository.save(lieu);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteLieu(@PathVariable Long id) {
        lieuRepository.deleteById(id);
    }
}
