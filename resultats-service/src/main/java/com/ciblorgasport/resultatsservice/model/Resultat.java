package com.ciblorgasport.resultatsservice.model;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "resultats",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_resultat_epreuve_athlete", columnNames = {"epreuve_id", "athlete_id"}),
                @UniqueConstraint(name = "uk_resultat_epreuve_equipe", columnNames = {"epreuve_id", "equipe_id"})
        }
)
public class Resultat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultat")
    private Long id;

    @Column(name = "classement")
    private Integer classement;

    @Enumerated(EnumType.STRING)
    @Column(name = "medaille")
    private Medaille medaille;

    @Column(name = "qualification")
    private boolean qualification;

    @Column(name = "valeur_principale", length = 50)
    private String valeurPrincipale;

    @Column(name = "unite", length = 20)
    private String unite;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details_performance", columnDefinition = "jsonb")
    private Map<String, Object> detailsPerformance;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_performance")
    private TypePerformance typePerformance;

    @Column(name = "athlete_id")
    private Long athleteId;

    @Column(name = "equipe_id")
    private Long equipeId;

    @Column(name = "epreuve_id", nullable = false)
    private Long epreuveId;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private ResultatStatut statut;

    @Column(name = "published")
    private boolean published;

    public Resultat() {
    }

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
