package com.ciblorgasport.resultatsservice.service.calcul.strategies;

import com.ciblorgasport.resultatsservice.model.Medaille;
import com.ciblorgasport.resultatsservice.model.Resultat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Stratégie de classement pour la NATATION_ARTISTIQUE.
 * Tri : note DÉCROISSANTE. Si detailsPerformance contient "note_technique" et
 * "note_artistique", la note finale = somme des deux. Sinon valeurPrincipale.
 * Qualifications : QUALIFICATION → top 12.
 */
@Component
public class NatationArtistiqueClassementStrategy implements ClassementStrategy {

    @Override
    public void trierResultats(List<Resultat> resultats) {
        resultats.sort((r1, r2) -> {
            double n1 = calculerNote(r1);
            double n2 = calculerNote(r2);
            return Double.compare(n2, n1); // décroissant
        });
    }

    private double calculerNote(Resultat r) {
        Map<String, Object> details = r.getDetailsPerformance();
        if (details != null
                && details.containsKey("note_technique")
                && details.containsKey("note_artistique")) {
            double tech = ((Number) details.get("note_technique")).doubleValue();
            double art  = ((Number) details.get("note_artistique")).doubleValue();
            return tech + art;
        }
        return Double.parseDouble(r.getValeurPrincipale());
    }

    @Override
    public void attribuerClassements(List<Resultat> resultats) {
        int rang = 1;
        for (int i = 0; i < resultats.size(); i++) {
            if (i > 0 && calculerNote(resultats.get(i)) == calculerNote(resultats.get(i - 1))) {
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
            int nbQualifies = 12;
            for (int i = 0; i < resultats.size(); i++) {
                resultats.get(i).setQualification(i < nbQualifies);
            }
        }
    }
}
