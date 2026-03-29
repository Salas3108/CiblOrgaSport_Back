package com.ciblorgasport.lieuservice.controller;

import com.ciblorgasport.lieuservice.model.Lieu;
import com.ciblorgasport.lieuservice.service.LieuService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lieux")
public class LieuController {
    private final LieuService lieuService;

    public LieuController(LieuService lieuService) {
        this.lieuService = lieuService;
    }

    @GetMapping
    public List<Lieu> getAllLieux() {
        return lieuService.getAllLieux();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lieu> getLieuById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(lieuService.getLieuById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<Lieu> createLieu(@RequestBody Lieu lieu) {
        return ResponseEntity.status(201).body(lieuService.createLieu(lieu));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<Lieu> updateLieu(@PathVariable Long id, @RequestBody Lieu lieuDetails) {
        try {
            return ResponseEntity.ok(lieuService.updateLieu(id, lieuDetails));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
    public ResponseEntity<Void> deleteLieu(@PathVariable Long id) {
        lieuService.deleteLieu(id);
        return ResponseEntity.noContent().build();
    }
}
