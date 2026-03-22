package com.ciblorgasport.resultatsservice.dto.response;

import com.ciblorgasport.resultatsservice.client.dto.AthleteInfoDto;
import com.ciblorgasport.resultatsservice.client.dto.EquipeInfoDto;

import java.util.List;

/**
 * Réponse de l'endpoint GET /epreuves/{id}/contexte.
 * Contient les métadonnées de l'épreuve + la liste des participants.
 */
public class EpreuveContexteResponse {

    private Long epreuveId;
    private String nom;
    private String discipline;
    private String niveauEpreuve;
    private String typeEpreuve;
    private List<AthleteInfoDto> athletes;
    private List<EquipeInfoDto> equipes;

    public Long getEpreuveId() { return epreuveId; }
    public void setEpreuveId(Long epreuveId) { this.epreuveId = epreuveId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDiscipline() { return discipline; }
    public void setDiscipline(String discipline) { this.discipline = discipline; }

    public String getNiveauEpreuve() { return niveauEpreuve; }
    public void setNiveauEpreuve(String niveauEpreuve) { this.niveauEpreuve = niveauEpreuve; }

    public String getTypeEpreuve() { return typeEpreuve; }
    public void setTypeEpreuve(String typeEpreuve) { this.typeEpreuve = typeEpreuve; }

    public List<AthleteInfoDto> getAthletes() { return athletes; }
    public void setAthletes(List<AthleteInfoDto> athletes) { this.athletes = athletes; }

    public List<EquipeInfoDto> getEquipes() { return equipes; }
    public void setEquipes(List<EquipeInfoDto> equipes) { this.equipes = equipes; }
}
