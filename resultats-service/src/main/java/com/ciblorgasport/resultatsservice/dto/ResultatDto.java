package com.ciblorgasport.resultatsservice.dto;

import java.util.Map;

import com.ciblorgasport.resultatsservice.model.Medaille;
import com.ciblorgasport.resultatsservice.model.ResultatStatut;
import com.ciblorgasport.resultatsservice.model.TypePerformance;

public class ResultatDto {
    private Long id;
    private Integer classement;
    private Medaille medaille;
    private boolean qualification;
    private String valeurPrincipale;
    private String unite;
    private Map<String, Object> detailsPerformance;
    private TypePerformance typePerformance;
    private Long athleteId;
    private Long equipeId;
    private Long epreuveId;
    private ResultatStatut statut;
    private boolean published;

    // Champs enrichis (nullable — absents si appel sans contexte inter-service)
    private String athleteNom;
    private String athletePrenom;
    private String athletePays;
    private String equipeNom;
    private String equipePays;
    private String epreuveNom;
    private String discipline;
    private String niveauEpreuve;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getClassement() {
        return classement;
    }

    public void setClassement(Integer classement) {
        this.classement = classement;
    }

    public Medaille getMedaille() {
        return medaille;
    }

    public void setMedaille(Medaille medaille) {
        this.medaille = medaille;
    }

    public boolean isQualification() {
        return qualification;
    }

    public void setQualification(boolean qualification) {
        this.qualification = qualification;
    }

    public String getValeurPrincipale() {
        return valeurPrincipale;
    }

    public void setValeurPrincipale(String valeurPrincipale) {
        this.valeurPrincipale = valeurPrincipale;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public Map<String, Object> getDetailsPerformance() {
        return detailsPerformance;
    }

    public void setDetailsPerformance(Map<String, Object> detailsPerformance) {
        this.detailsPerformance = detailsPerformance;
    }

    public TypePerformance getTypePerformance() {
        return typePerformance;
    }

    public void setTypePerformance(TypePerformance typePerformance) {
        this.typePerformance = typePerformance;
    }

    public Long getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(Long athleteId) {
        this.athleteId = athleteId;
    }

    public Long getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(Long equipeId) {
        this.equipeId = equipeId;
    }

    public Long getEpreuveId() {
        return epreuveId;
    }

    public void setEpreuveId(Long epreuveId) {
        this.epreuveId = epreuveId;
    }

    public ResultatStatut getStatut() {
        return statut;
    }

    public void setStatut(ResultatStatut statut) {
        this.statut = statut;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getAthleteNom() { return athleteNom; }
    public void setAthleteNom(String athleteNom) { this.athleteNom = athleteNom; }

    public String getAthletePrenom() { return athletePrenom; }
    public void setAthletePrenom(String athletePrenom) { this.athletePrenom = athletePrenom; }

    public String getAthletePays() { return athletePays; }
    public void setAthletePays(String athletePays) { this.athletePays = athletePays; }

    public String getEquipeNom() { return equipeNom; }
    public void setEquipeNom(String equipeNom) { this.equipeNom = equipeNom; }

    public String getEquipePays() { return equipePays; }
    public void setEquipePays(String equipePays) { this.equipePays = equipePays; }

    public String getEpreuveNom() { return epreuveNom; }
    public void setEpreuveNom(String epreuveNom) { this.epreuveNom = epreuveNom; }

    public String getDiscipline() { return discipline; }
    public void setDiscipline(String discipline) { this.discipline = discipline; }

    public String getNiveauEpreuve() { return niveauEpreuve; }
    public void setNiveauEpreuve(String niveauEpreuve) { this.niveauEpreuve = niveauEpreuve; }
}
