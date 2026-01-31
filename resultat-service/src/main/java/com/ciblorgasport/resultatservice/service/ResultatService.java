package com.ciblorgasport.resultatservice.service;

import com.ciblorgasport.resultatservice.dto.CreerResultatRequest;
import com.ciblorgasport.resultatservice.dto.ModifierResultatRequest;
import com.ciblorgasport.resultatservice.dto.ResultatDTO;
import com.ciblorgasport.resultatservice.dto.ValiderResultatRequest;
import com.ciblorgasport.resultatservice.entity.HistoriqueResultat;
import com.ciblorgasport.resultatservice.entity.Resultat;
import com.ciblorgasport.resultatservice.entity.StatusResultat;
import com.ciblorgasport.resultatservice.repository.HistoriqueResultatRepository;
import com.ciblorgasport.resultatservice.repository.ResultatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultatService {
    
    private final ResultatRepository resultatRepository;
    private final HistoriqueResultatRepository historiqueRepository;
    private static final Logger log = LoggerFactory.getLogger(ResultatService.class);
    
    public ResultatService(ResultatRepository resultatRepository, HistoriqueResultatRepository historiqueRepository) {
        this.resultatRepository = resultatRepository;
        this.historiqueRepository = historiqueRepository;
    }
    
    /**
     * Saisir manuellement un résultat d'épreuve
     */
    @Transactional
    public ResultatDTO saisirResultat(CreerResultatRequest request, Long commissaireId) {
        log.info("Saisie d'un résultat pour l'épreuve {} et l'athlète {} par le commissaire {}", 
                 request.getEpreuveId(), request.getAthleteId(), commissaireId);
        
        // Vérifier qu'il n'existe pas déjà un résultat pour cette épreuve/athlète
        if (resultatRepository.findByEpreuveIdAndAthleteId(
                request.getEpreuveId(), request.getAthleteId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Un résultat existe déjà pour cette épreuve et cet athlète");
        }
        
        Resultat resultat = new Resultat();
        resultat.setEpreuveId(request.getEpreuveId());
        resultat.setAthleteId(request.getAthleteId());
        resultat.setClassement(request.getClassement());
        resultat.setTemps(request.getTemps());
        resultat.setDistance(request.getDistance());
        resultat.setPoints(request.getPoints());
        resultat.setObservations(request.getObservations());
        resultat.setSaisieParId(commissaireId);
        resultat.setStatus(StatusResultat.SAISI);
        resultat.setDateCreation(LocalDateTime.now());
        resultat.setDateModification(LocalDateTime.now());
        
        Resultat saved = resultatRepository.save(resultat);
        log.info("Résultat créé avec l'ID: {}", saved.getId());
        
        return convertToDTO(saved);
    }
    
    /**
     * Récupérer tous les résultats d'une épreuve
     */
    @Transactional(readOnly = true)
    public List<ResultatDTO> getResultatsEpreuve(Long epreuveId) {
        log.debug("Récupération des résultats pour l'épreuve {}", epreuveId);
        return resultatRepository.findByEpreuveIdOrderedByClassement(epreuveId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer un résultat par son ID
     */
    @Transactional(readOnly = true)
    public ResultatDTO getResultat(Long resultatId) {
        log.debug("Récupération du résultat {}", resultatId);
        Resultat resultat = resultatRepository.findById(resultatId)
                .orElseThrow(() -> new IllegalArgumentException("Résultat non trouvé: " + resultatId));
        return convertToDTO(resultat);
    }
    
    /**
     * Modifier un résultat existant
     */
    @Transactional
    public ResultatDTO modifierResultat(Long resultatId, ModifierResultatRequest request, Long commissaireId) {
        log.info("Modification du résultat {} par le commissaire {}", resultatId, commissaireId);
        
        Resultat resultat = resultatRepository.findById(resultatId)
                .orElseThrow(() -> new IllegalArgumentException("Résultat non trouvé: " + resultatId));
        
        // Vérifier que le statut permet la modification
        if (!canModify(resultat.getStatus())) {
            throw new IllegalStateException(
                    "Impossible de modifier un résultat avec le statut: " + resultat.getStatus());
        }
        
        // Mettre à jour les champs
        if (request.getClassement() != null) {
            resultat.setClassement(request.getClassement());
        }
        if (request.getTemps() != null) {
            resultat.setTemps(request.getTemps());
        }
        if (request.getDistance() != null) {
            resultat.setDistance(request.getDistance());
        }
        if (request.getPoints() != null) {
            resultat.setPoints(request.getPoints());
        }
        if (request.getObservations() != null) {
            resultat.setObservations(request.getObservations());
        }
        
        resultat.setDateModification(LocalDateTime.now());
        Resultat updated = resultatRepository.save(resultat);
        log.info("Résultat {} modifié", resultatId);
        
        return convertToDTO(updated);
    }
    
    /**
     * Valider un résultat (changement de statut)
     */
    @Transactional
    public ResultatDTO validerResultat(Long resultatId, ValiderResultatRequest request, Long valideurId) {
        log.info("Validation du résultat {} avec le statut {} par {}", 
                 resultatId, request.getStatus(), valideurId);
        
        Resultat resultat = resultatRepository.findById(resultatId)
                .orElseThrow(() -> new IllegalArgumentException("Résultat non trouvé: " + resultatId));
        
        StatusResultat ancienStatus = resultat.getStatus();
        
        // Enregistrer dans l'historique
        HistoriqueResultat historique = new HistoriqueResultat();
        historique.setResultatId(resultatId);
        historique.setAncienStatus(ancienStatus);
        historique.setNouveauStatus(request.getStatus());
        historique.setModifiePar(valideurId);
        historique.setRaison(request.getRaison());
        historique.setDateModification(LocalDateTime.now());
        
        historiqueRepository.save(historique);
        
        resultat.setStatus(request.getStatus());
        resultat.setDateModification(LocalDateTime.now());
        Resultat updated = resultatRepository.save(resultat);
        
        log.info("Résultat {} validé avec le statut {}", resultatId, request.getStatus());
        
        return convertToDTO(updated);
    }
    
    /**
     * Supprimer un résultat
     */
    @Transactional
    public void supprimerResultat(Long resultatId) {
        log.info("Suppression du résultat {}", resultatId);
        
        Resultat resultat = resultatRepository.findById(resultatId)
                .orElseThrow(() -> new IllegalArgumentException("Résultat non trouvé: " + resultatId));
        
        // Vérifier que le statut permet la suppression
        if (resultat.getStatus() == StatusResultat.VALIDE) {
            throw new IllegalStateException(
                    "Impossible de supprimer un résultat validé");
        }
        
        resultatRepository.delete(resultat);
        log.info("Résultat {} supprimé", resultatId);
    }
    
    /**
     * Récupérer l'historique des modifications d'un résultat
     */
    @Transactional(readOnly = true)
    public List<HistoriqueResultat> getHistorique(Long resultatId) {
        log.debug("Récupération de l'historique du résultat {}", resultatId);
        return historiqueRepository.findByResultatId(resultatId);
    }
    
    /**
     * Récupérer les résultats saisis par un commissaire
     */
    @Transactional(readOnly = true)
    public List<ResultatDTO> getResultatsParCommissaire(Long commissaireId) {
        log.debug("Récupération des résultats saisis par le commissaire {}", commissaireId);
        return resultatRepository.findBySaisieParId(commissaireId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les résultats en attente de validation
     */
    @Transactional(readOnly = true)
    public List<ResultatDTO> getResultatsEnAttente() {
        log.debug("Récupération des résultats en attente de validation");
        return resultatRepository.findByStatus(StatusResultat.SAISI)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Vérifier si un statut permet la modification
     */
    private boolean canModify(StatusResultat status) {
        return status == StatusResultat.SAISI || status == StatusResultat.EN_CORRECTION;
    }
    
    /**
     * Convertir une entité Resultat en DTO
     */
    private ResultatDTO convertToDTO(Resultat resultat) {
        ResultatDTO dto = new ResultatDTO();
        dto.setId(resultat.getId());
        dto.setEpreuveId(resultat.getEpreuveId());
        dto.setAthleteId(resultat.getAthleteId());
        dto.setClassement(resultat.getClassement());
        dto.setTemps(resultat.getTemps());
        dto.setDistance(resultat.getDistance());
        dto.setPoints(resultat.getPoints());
        dto.setStatus(resultat.getStatus());
        dto.setSaisieParId(resultat.getSaisieParId());
        dto.setObservations(resultat.getObservations());
        dto.setDateCreation(resultat.getDateCreation());
        dto.setDateModification(resultat.getDateModification());
        return dto;
    }
}