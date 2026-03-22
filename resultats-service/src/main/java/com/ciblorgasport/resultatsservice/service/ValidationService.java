package com.ciblorgasport.resultatsservice.service;

import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.model.ResultatStatut;
import com.ciblorgasport.resultatsservice.repository.ResultatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gère la validation des résultats (EN_ATTENTE → VALIDE).
 */
@Service
public class ValidationService {

    private static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    private final ResultatRepository resultatRepository;

    public ValidationService(ResultatRepository resultatRepository) {
        this.resultatRepository = resultatRepository;
    }

    /**
     * Valide un résultat individuel.
     */
    @Transactional
    public void valider(Long resultatId) {
        Resultat resultat = resultatRepository.findById(resultatId)
                .orElseThrow(() -> new IllegalArgumentException("Résultat " + resultatId + " introuvable"));

        resultat.setStatut(ResultatStatut.VALIDE);
        resultatRepository.save(resultat);
        log.info("Résultat {} validé", resultatId);
    }

    /**
     * Valide tous les résultats EN_ATTENTE d'une épreuve.
     * Retourne la liste des résultats validés.
     */
    @Transactional
    public List<Resultat> validerTout(Long epreuveId) {
        List<Resultat> resultats = resultatRepository.findByEpreuveId(epreuveId)
                .stream()
                .filter(r -> r.getStatut() == ResultatStatut.EN_ATTENTE)
                .toList();

        resultats.forEach(r -> r.setStatut(ResultatStatut.VALIDE));
        List<Resultat> saved = resultatRepository.saveAll(resultats);
        log.info("{} résultats validés pour l'épreuve {}", saved.size(), epreuveId);
        return saved;
    }
}
