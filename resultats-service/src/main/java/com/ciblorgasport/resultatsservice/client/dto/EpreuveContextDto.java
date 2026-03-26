package com.ciblorgasport.resultatsservice.client.dto;

import java.util.Set;

/**
 * Miroir local des données d'épreuve issues de l'event-service.
 * Contient uniquement les champs nécessaires au calcul des résultats.
 */
public class EpreuveContextDto {

    private Long id;
    private String nom;
    private String typeEpreuve;    // INDIVIDUELLE, COLLECTIVE
    private String niveauEpreuve;  // QUALIFICATION, QUART_DE_FINALE, DEMI_FINALE, FINALE
    private String discipline;     // NATATION, PLONGEON, WATER_POLO, NATATION_ARTISTIQUE, EAU_LIBRE
    private Set<Long> athleteIds;
    private Set<Long> equipeIds;

    public EpreuveContextDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTypeEpreuve() { return typeEpreuve; }
    public void setTypeEpreuve(String typeEpreuve) { this.typeEpreuve = typeEpreuve; }

    public String getNiveauEpreuve() { return niveauEpreuve; }
    public void setNiveauEpreuve(String niveauEpreuve) { this.niveauEpreuve = niveauEpreuve; }

    public String getDiscipline() { return discipline; }
    public void setDiscipline(String discipline) { this.discipline = discipline; }

    public Set<Long> getAthleteIds() { return athleteIds; }
    public void setAthleteIds(Set<Long> athleteIds) { this.athleteIds = athleteIds; }

    public Set<Long> getEquipeIds() { return equipeIds; }
    public void setEquipeIds(Set<Long> equipeIds) { this.equipeIds = equipeIds; }
}
