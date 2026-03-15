package com.ciblorgasport.resultatsservice.dto.request;

import java.util.List;

/**
 * Requête de saisie bulk : le commissaire envoie toutes les performances d'une épreuve en une fois.
 */
public class BulkResultatRequest {

    private List<PerformanceEntryDto> performances;

    public List<PerformanceEntryDto> getPerformances() { return performances; }
    public void setPerformances(List<PerformanceEntryDto> performances) { this.performances = performances; }
}
