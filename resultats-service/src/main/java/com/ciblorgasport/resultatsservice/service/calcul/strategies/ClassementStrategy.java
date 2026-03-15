package com.ciblorgasport.resultatsservice.service.calcul.strategies;

import com.ciblorgasport.resultatsservice.model.Resultat;

import java.util.List;

/**
 * Interface définissant le contrat pour les stratégies de classement.
 * Chaque discipline aquatique implémente sa propre logique.
 */
public interface ClassementStrategy {

    /**
     * Trier les résultats selon les règles de la discipline.
     * NATATION/EAU_LIBRE : temps croissant (plus petit = meilleur)
     * PLONGEON/NATATION_ARTISTIQUE : points décroissant (plus grand = meilleur)
     * WATER_POLO : buts marqués décroissant
     */
    void trierResultats(List<Resultat> resultats);

    /**
     * Attribuer les classements 1, 2, 3, … avec gestion des ex aequo.
     * Deux résultats égaux → même rang, le rang suivant saute.
     */
    void attribuerClassements(List<Resultat> resultats);

    /**
     * Attribuer médailles (si FINALE) ou marquer les qualifiés (sinon).
     * Doit réinitialiser médaille et qualification avant d'attribuer.
     */
    void attribuerMedaillesEtQualification(List<Resultat> resultats, String niveauEpreuve);

    /**
     * Méthode template : orchestre le calcul complet.
     * Appelée par ClassementService.
     */
    default void calculer(List<Resultat> resultats, String niveauEpreuve) {
        trierResultats(resultats);
        attribuerClassements(resultats);
        attribuerMedaillesEtQualification(resultats, niveauEpreuve);
    }
}
