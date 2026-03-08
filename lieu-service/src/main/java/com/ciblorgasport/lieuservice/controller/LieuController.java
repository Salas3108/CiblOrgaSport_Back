package com.ciblorgasport.lieuservice.controller;

import com.ciblorgasport.lieuservice.model.Lieu;
import com.ciblorgasport.lieuservice.service.LieuService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lieux")
@PreAuthorize("hasRole('ADMIN') or hasRole('COMMISSAIRE')")
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
    public Lieu getLieuById(@PathVariable Long id) {
        return lieuService.getLieuById(id);
    }

    @PostMapping
    public Lieu createLieu(@RequestBody Lieu lieu) {
        return lieuService.createLieu(lieu);
    }

    @PutMapping("/{id}")
    public Lieu updateLieu(@PathVariable Long id, @RequestBody Lieu lieuDetails) {
        return lieuService.updateLieu(id, lieuDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteLieu(@PathVariable Long id) {
        lieuService.deleteLieu(id);
    }
}
