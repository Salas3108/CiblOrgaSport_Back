package com.ciblorgasport.participantsservice.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO Equipe.
 */
public class EquipeDto {
    private Long id;
    private String nom;
    private String pays;
    private List<Long> athleteIds = new ArrayList<>();

    public EquipeDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public List<Long> getAthleteIds() {
        return athleteIds;
    }

    public void setAthleteIds(List<Long> athleteIds) {
        this.athleteIds = athleteIds;
    }
}
