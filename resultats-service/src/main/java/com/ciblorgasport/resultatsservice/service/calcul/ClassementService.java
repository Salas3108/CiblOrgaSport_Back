package com.ciblorgasport.resultatsservice.service.calcul;

import com.ciblorgasport.resultatsservice.client.EventServiceClient;
import com.ciblorgasport.resultatsservice.client.dto.EpreuveContextDto;
import com.ciblorgasport.resultatsservice.model.Discipline;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.repository.ResultatRepository;
import com.ciblorgasport.resultatsservice.service.calcul.strategies.ClassementStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Orchestrateur du calcul de classement.
 * Sélectionne la bonne stratégie selon la discipline de l'épreuve,
 * puis déclenche tri + classement + médailles/qualifications.
 */
@Service
public class ClassementService {

    private static final Logger log = LoggerFactory.getLogger(ClassementService.class);

    private final ResultatRepository resultatRepository;
    private final EventServiceClient eventServiceClient;
    private final Map<Discipline, ClassementStrategy> strategies;

    public ClassementService(ResultatRepository resultatRepository,
                              EventServiceClient eventServiceClient,
                              Map<Discipline, ClassementStrategy> strategies) {
        this.resultatRepository = resultatRepository;
        this.eventServiceClient = eventServiceClient;
        this.strategies = strategies;
    }

    /**
     * Calcule et sauvegarde le classement complet d'une épreuve.
     * Appelé automatiquement après chaque saisie de performance.
     */
    @Transactional
    public void calculerClassement(Long epreuveId) {
        log.info("Calcul du classement pour l'épreuve {}", epreuveId);

        List<Resultat> resultats = resultatRepository.findByEpreuveId(epreuveId);
        if (resultats.isEmpty()) {
            log.warn("Aucun résultat trouvé pour l'épreuve {}", epreuveId);
            return;
        }

        EpreuveContextDto ctx = eventServiceClient.getEpreuveContext(epreuveId);
        if (ctx == null || ctx.getDiscipline() == null) {
            log.warn("Impossible de récupérer la discipline pour l'épreuve {} — classement non calculé", epreuveId);
            return;
        }

        Discipline discipline;
        try {
            discipline = Discipline.valueOf(ctx.getDiscipline());
        } catch (IllegalArgumentException e) {
            log.warn("Discipline inconnue '{}' pour l'épreuve {} — classement non calculé",
                    ctx.getDiscipline(), epreuveId);
            return;
        }

        ClassementStrategy strategy = strategies.get(discipline);
        if (strategy == null) {
            log.warn("Aucune stratégie pour la discipline {} — classement non calculé", discipline);
            return;
        }

        log.info("Stratégie {} appliquée pour discipline {}", strategy.getClass().getSimpleName(), discipline);

        try {
            strategy.calculer(resultats, ctx.getNiveauEpreuve());
            resultatRepository.saveAll(resultats);
            log.info("Classement sauvegardé pour {} résultats (épreuve {})", resultats.size(), epreuveId);
        } catch (Exception e) {
            log.error("Erreur lors du calcul du classement pour l'épreuve {} (discipline {}) : {}",
                    epreuveId, discipline, e.getMessage());
            throw new IllegalArgumentException(
                    "Format de performance invalide pour la discipline " + discipline +
                    ". Attendu : " + getFormatAttendu(discipline) +
                    ". Reçu : \"" + resultats.get(0).getValeurPrincipale() + "\"");
        }
    }

    /**
     * Recalcule le classement en utilisant un contexte déjà récupéré (évite un appel HTTP supplémentaire).
     */
    private String getFormatAttendu(Discipline discipline) {
        return switch (discipline) {
            case NATATION, EAU_LIBRE -> "nombre décimal en secondes (ex: 49.95)";
            case WATER_POLO -> "score au format buts-buts (ex: 12-8)";
            case PLONGEON, NATATION_ARTISTIQUE -> "nombre décimal de points (ex: 89.50)";
        };
    }

    @Transactional
    public void calculerClassementAvecContexte(Long epreuveId, EpreuveContextDto ctx) {
        List<Resultat> resultats = resultatRepository.findByEpreuveId(epreuveId);
        if (resultats.isEmpty() || ctx == null || ctx.getDiscipline() == null) return;

        Discipline discipline;
        try {
            discipline = Discipline.valueOf(ctx.getDiscipline());
        } catch (IllegalArgumentException e) {
            log.warn("Discipline inconnue '{}' — classement non calculé", ctx.getDiscipline());
            return;
        }

        ClassementStrategy strategy = strategies.get(discipline);
        if (strategy == null) return;

        try {
            strategy.calculer(resultats, ctx.getNiveauEpreuve());
            resultatRepository.saveAll(resultats);
        } catch (Exception e) {
            log.error("Erreur calcul classement épreuve {} discipline {} : {}", epreuveId, discipline, e.getMessage());
            throw new IllegalArgumentException(
                    "Format de performance invalide pour la discipline " + discipline +
                    ". Attendu : " + getFormatAttendu(discipline) +
                    ". Reçu : \"" + resultats.get(0).getValeurPrincipale() + "\"");
        }
    }
}
