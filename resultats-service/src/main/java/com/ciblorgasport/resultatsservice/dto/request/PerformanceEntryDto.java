package com.ciblorgasport.resultatsservice.dto.request;

import java.util.Map;

/**
 * Entrée unitaire dans une saisie bulk.
 * Le commissaire fournit uniquement la valeur de performance.
 * Le classement, médaille et qualification sont calculés automatiquement.
 */
public class PerformanceEntryDto {

    private Long athleteId;
    private Long equipeId;
    private String valeurPrincipale;
    private Map<String, Object> detailsPerformance;

    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }

    public Long getEquipeId() { return equipeId; }
    public void setEquipeId(Long equipeId) { this.equipeId = equipeId; }

    public String getValeurPrincipale() { return valeurPrincipale; }
    public void setValeurPrincipale(String valeurPrincipale) { this.valeurPrincipale = valeurPrincipale; }

    public Map<String, Object> getDetailsPerformance() { return detailsPerformance; }
    public void setDetailsPerformance(Map<String, Object> detailsPerformance) { this.detailsPerformance = detailsPerformance; }
}
