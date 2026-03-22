package com.ciblorgasport.resultatsservice.service.calcul.strategies;

import com.ciblorgasport.resultatsservice.model.Medaille;
import com.ciblorgasport.resultatsservice.model.Resultat;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Stratégie de classement pour le WATER_POLO.
 * Format valeurPrincipale : "12-10" (buts marqués - buts concédés).
 * Tri : buts marqués DÉCROISSANT (plus = meilleur).
 * FINALE : OR/ARGENT pour les 2 équipes.
 */
@Component
public class WaterPoloClassementStrategy implements ClassementStrategy {

    @Override
    public void trierResultats(List<Resultat> resultats) {
        resultats.sort((r1, r2) -> {
            int buts1 = extraireButsMarques(r1.getValeurPrincipale());
            int buts2 = extraireButsMarques(r2.getValeurPrincipale());
            return Integer.compare(buts2, buts1); // décroissant
        });
    }

    private int extraireButsMarques(String score) {
        String[] parts = score.split("-");
        return Integer.parseInt(parts[0].trim());
    }

    @Override
    public void attribuerClassements(List<Resultat> resultats) {
        int rang = 1;
        for (int i = 0; i < resultats.size(); i++) {
            if (i > 0 && extraireButsMarques(resultats.get(i).getValeurPrincipale())
                    == extraireButsMarques(resultats.get(i - 1).getValeurPrincipale())) {
                resultats.get(i).setClassement(resultats.get(i - 1).getClassement());
            } else {
                resultats.get(i).setClassement(rang);
            }
            rang++;
        }
    }

    @Override
    public void attribuerMedaillesEtQualification(List<Resultat> resultats, String niveauEpreuve) {
        resultats.forEach(r -> {
            r.setMedaille(null);
            r.setQualification(false);
        });

        if ("FINALE".equals(niveauEpreuve)) {
            if (resultats.size() >= 1) resultats.get(0).setMedaille(Medaille.OR);
            if (resultats.size() >= 2) resultats.get(1).setMedaille(Medaille.ARGENT);
        } else {
            // Phases de poule / quart / demi : tous qualifiés (logique tournoi)
            resultats.forEach(r -> r.setQualification(true));
        }
    }
}
