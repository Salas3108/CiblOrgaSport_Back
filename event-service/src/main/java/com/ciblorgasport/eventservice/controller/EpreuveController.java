package com.ciblorgasport.eventservice.controller;

import com.ciblorgasport.eventservice.model.Epreuve;
import com.ciblorgasport.eventservice.repository.EpreuveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/epreuves")
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
    public Epreuve getEpreuveById(@PathVariable Long id) {
        return epreuveRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Epreuve updateEpreuve(@PathVariable Long id, @RequestBody Epreuve epreuveDetails) {
        Epreuve epreuve = epreuveRepository.findById(id).orElse(null);
        if (epreuve != null) {
            epreuve.setNom(epreuveDetails.getNom());
            epreuve.setDescription(epreuveDetails.getDescription());
            return epreuveRepository.save(epreuve);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteEpreuve(@PathVariable Long id) {
        epreuveRepository.deleteById(id);
    }
}
