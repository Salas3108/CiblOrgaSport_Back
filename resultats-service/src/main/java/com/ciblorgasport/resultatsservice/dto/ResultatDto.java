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
}
