package com.ciblorgasport.lieuservice.service;

import com.ciblorgasport.lieuservice.model.Lieu;
import com.ciblorgasport.lieuservice.repository.LieuRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LieuService {
    private final LieuRepository lieuRepository;

    public LieuService(LieuRepository lieuRepository) {
        this.lieuRepository = lieuRepository;
    }

    public List<Lieu> getAllLieux() {
        return lieuRepository.findAll();
    }

    public Lieu getLieuById(Long id) {
        return lieuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lieu not found: " + id));
    }

    public Lieu createLieu(Lieu lieu) {
        return lieuRepository.save(lieu);
    }

    public Lieu updateLieu(Long id, Lieu lieuDetails) {
        Lieu lieu = lieuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lieu not found: " + id));

        lieu.setNom(lieuDetails.getNom());
        lieu.setAdresse(lieuDetails.getAdresse());
        lieu.setVille(lieuDetails.getVille());
        lieu.setCodePostal(lieuDetails.getCodePostal());
        lieu.setPays(lieuDetails.getPays());
        lieu.setCapaciteSpectateurs(lieuDetails.getCapaciteSpectateurs());
        return lieuRepository.save(lieu);
    }

    public void deleteLieu(Long id) {
        lieuRepository.deleteById(id);
    }
}
