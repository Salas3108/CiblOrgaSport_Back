package com.ciblorgasport.resultatservice.controller;

import com.ciblorgasport.resultatservice.dto.CreerResultatRequest;
import com.ciblorgasport.resultatservice.dto.ModifierResultatRequest;
import com.ciblorgasport.resultatservice.dto.ResultatDTO;
import com.ciblorgasport.resultatservice.dto.ValiderResultatRequest;
import com.ciblorgasport.resultatservice.entity.HistoriqueResultat;
import com.ciblorgasport.resultatservice.service.ResultatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/resultats")
public class ResultatController {
    
    private final ResultatService resultatService;
    private static final Logger log = LoggerFactory.getLogger(ResultatController.class);
    
    public ResultatController(ResultatService resultatService) {
        this.resultatService = resultatService;
    }
    
    /**
     * POST /api/resultats - Saisir manuellement un résultat d'épreuve
     * Permissions: COMMISSAIRE, ADMIN
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('COMMISSAIRE', 'ADMIN')")
    public ResponseEntity<ResultatDTO> creerResultat(
            @Valid @RequestBody CreerResultatRequest request,
            @RequestHeader("X-User-Id") Long commissaireId) {
        log.info("Création d'un nouveau résultat pour l'épreuve {}", request.getEpreuveId());
        ResultatDTO resultat = resultatService.saisirResultat(request, commissaireId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultat);
    }
    
    /**
     * GET /api/resultats/{id} - Récupérer un résultat par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResultatDTO> getResultat(@PathVariable Long id) {
        log.info("Récupération du résultat {}", id);
        ResultatDTO resultat = resultatService.getResultat(id);
        return ResponseEntity.ok(resultat);
    }
    
    /**
     * GET /api/resultats/epreuve/{epreuveId} - Récupérer tous les résultats d'une épreuve
     */
    @GetMapping("/epreuve/{epreuveId}")
    public ResponseEntity<List<ResultatDTO>> getResultatsEpreuve(@PathVariable Long epreuveId) {
        log.info("Récupération des résultats de l'épreuve {}", epreuveId);
        List<ResultatDTO> resultats = resultatService.getResultatsEpreuve(epreuveId);
        return ResponseEntity.ok(resultats);
    }
    
    /**
     * PUT /api/resultats/{id} - Modifier un résultat existant
     * Permissions: COMMISSAIRE, ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMMISSAIRE', 'ADMIN')")
    public ResponseEntity<ResultatDTO> modifierResultat(
            @PathVariable Long id,
            @Valid @RequestBody ModifierResultatRequest request,
            @RequestHeader("X-User-Id") Long commissaireId) {
        log.info("Modification du résultat {}", id);
        ResultatDTO resultat = resultatService.modifierResultat(id, request, commissaireId);
        return ResponseEntity.ok(resultat);
    }
    
    /**
     * PATCH /api/resultats/{id}/valider - Valider un résultat (changer son statut)
     * Permissions: ADMIN
     */
    @PatchMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultatDTO> validerResultat(
            @PathVariable Long id,
            @Valid @RequestBody ValiderResultatRequest request,
            @RequestHeader("X-User-Id") Long valideurId) {
        log.info("Validation du résultat {} avec le statut {}", id, request.getStatus());
        ResultatDTO resultat = resultatService.validerResultat(id, request, valideurId);
        return ResponseEntity.ok(resultat);
    }
    
    /**
     * DELETE /api/resultats/{id} - Supprimer un résultat
     * Permissions: COMMISSAIRE, ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMMISSAIRE', 'ADMIN')")
    public ResponseEntity<Void> supprimerResultat(@PathVariable Long id) {
        log.info("Suppression du résultat {}", id);
        resultatService.supprimerResultat(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/resultats/{id}/historique - Récupérer l'historique des modifications d'un résultat
     * Permissions: ADMIN
     */
    @GetMapping("/{id}/historique")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HistoriqueResultat>> getHistorique(@PathVariable Long id) {
        log.info("Récupération de l'historique du résultat {}", id);
        List<HistoriqueResultat> historique = resultatService.getHistorique(id);
        return ResponseEntity.ok(historique);
    }
    
    /**
     * GET /api/resultats/commissaire/{commissaireId} - Récupérer les résultats saisis par un commissaire
     * Permissions: ADMIN
     */
    @GetMapping("/commissaire/{commissaireId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ResultatDTO>> getResultatsParCommissaire(@PathVariable Long commissaireId) {
        log.info("Récupération des résultats du commissaire {}", commissaireId);
        List<ResultatDTO> resultats = resultatService.getResultatsParCommissaire(commissaireId);
        return ResponseEntity.ok(resultats);
    }
    
    /**
     * GET /api/resultats/en-attente - Récupérer les résultats en attente de validation
     * Permissions: ADMIN
     */
    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ResultatDTO>> getResultatsEnAttente() {
        log.info("Récupération des résultats en attente");
        List<ResultatDTO> resultats = resultatService.getResultatsEnAttente();
        return ResponseEntity.ok(resultats);
    }
    
    /**
     * Gestion des erreurs
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Erreur de validation: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        log.warn("Erreur d'état: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        log.error("Erreur serveur: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Une erreur s'est produite");
    }
}