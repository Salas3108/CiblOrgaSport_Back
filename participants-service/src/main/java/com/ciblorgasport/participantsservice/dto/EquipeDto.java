package com.ciblorgasport.participantsservice.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO Equipe avec username des athlètes inclus.
 */
public class EquipeDto {
    private Long id;
    private String nom;
    private String pays;

    // Map des IDs vers usernames
    private Map<Long, String> athleteIdUsernameMap = new HashMap<>();

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

    public Map<Long, String> getAthleteIdUsernameMap() {
        return athleteIdUsernameMap;
    }

    public void setAthleteIdUsernameMap(Map<Long, String> athleteIdUsernameMap) {
        this.athleteIdUsernameMap = athleteIdUsernameMap;
    }
}