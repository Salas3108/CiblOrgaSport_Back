package com.ciblorgasport.resultatsservice.service.calcul.strategies;

import com.ciblorgasport.resultatsservice.model.Medaille;
import com.ciblorgasport.resultatsservice.model.Resultat;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Stratégie de classement pour le PLONGEON.
 * Tri : points DÉCROISSANT (plus grand = meilleur).
 * Qualifications : QUALIFICATION → top 18, DEMI_FINALE → top 12.
 */
@Component
public class PlongeonClassementStrategy implements ClassementStrategy {

    @Override
    public void trierResultats(List<Resultat> resultats) {
        resultats.sort((r1, r2) -> {
            double p1 = Double.parseDouble(r1.getValeurPrincipale());
            double p2 = Double.parseDouble(r2.getValeurPrincipale());
            return Double.compare(p2, p1); // décroissant
        });
    }

    @Override
    public void attribuerClassements(List<Resultat> resultats) {
        int rang = 1;
        for (int i = 0; i < resultats.size(); i++) {
            if (i > 0 && Double.parseDouble(resultats.get(i).getValeurPrincipale())
                    == Double.parseDouble(resultats.get(i - 1).getValeurPrincipale())) {
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
            if (resultats.size() >= 3) resultats.get(2).setMedaille(Medaille.BRONZE);
        } else {
            int nbQualifies = "DEMI_FINALE".equals(niveauEpreuve) ? 12 : 18;
            for (int i = 0; i < resultats.size(); i++) {
                resultats.get(i).setQualification(i < nbQualifies);
            }
        }
    }
}
