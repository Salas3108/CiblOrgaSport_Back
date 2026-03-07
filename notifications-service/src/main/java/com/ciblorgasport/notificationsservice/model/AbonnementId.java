package com.ciblorgasport.notificationsservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AbonnementId implements Serializable {

    @Column(name = "id_spectateur")
    private Long idSpectateur;

    @Column(name = "id_competition")
    private Long idCompetition;

    public AbonnementId() {
    }

    public AbonnementId(Long idSpectateur, Long idCompetition) {
        this.idSpectateur = idSpectateur;
        this.idCompetition = idCompetition;
    }

    public Long getIdSpectateur() {
        return idSpectateur;
    }

    public void setIdSpectateur(Long idSpectateur) {
        this.idSpectateur = idSpectateur;
    }

    public Long getIdCompetition() {
        return idCompetition;
    }

    public void setIdCompetition(Long idCompetition) {
        this.idCompetition = idCompetition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbonnementId that = (AbonnementId) o;
        return Objects.equals(idSpectateur, that.idSpectateur) && Objects.equals(idCompetition, that.idCompetition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSpectateur, idCompetition);
    }
}
