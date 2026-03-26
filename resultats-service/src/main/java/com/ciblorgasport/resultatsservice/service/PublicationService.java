package com.ciblorgasport.resultatsservice.service;

import com.ciblorgasport.resultatsservice.dto.response.PublicationResponseDTO;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.model.ResultatStatut;
import com.ciblorgasport.resultatsservice.repository.ResultatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gère la publication des résultats (published = true).
 * Prérequis : tous les résultats doivent être VALIDE.
 */
@Service
public class PublicationService {

    private static final Logger log = LoggerFactory.getLogger(PublicationService.class);

    private final ResultatRepository resultatRepository;

    public PublicationService(ResultatRepository resultatRepository) {
        this.resultatRepository = resultatRepository;
    }

    /**
     * Publie tous les résultats VALIDE d'une épreuve.
     * Lance une exception si des résultats sont encore EN_ATTENTE.
     */
    @Transactional
    public PublicationResponseDTO publierEpreuve(Long epreuveId) {
        List<Resultat> resultats = resultatRepository.findByEpreuveId(epreuveId);

        if (resultats.isEmpty()) {
            throw new IllegalArgumentException("Aucun résultat à publier pour l'épreuve " + epreuveId);
        }

        long enAttente = resultats.stream()
                .filter(r -> r.getStatut() == ResultatStatut.EN_ATTENTE)
                .count();

        if (enAttente > 0) {
            throw new IllegalStateException(
                    enAttente + " résultat(s) encore EN_ATTENTE — validez-les avant de publier");
        }

        List<Resultat> aPublier = resultats.stream()
                .filter(r -> !r.isPublished())
                .toList();

        aPublier.forEach(r -> r.setPublished(true));
        resultatRepository.saveAll(aPublier);

        log.info("{} résultats publiés pour l'épreuve {}", aPublier.size(), epreuveId);

        PublicationResponseDTO response = new PublicationResponseDTO();
        response.setEpreuveId(epreuveId);
        response.setNbResultatsPublies(aPublier.size());
        response.setSuccess(true);
        return response;
    }
}
