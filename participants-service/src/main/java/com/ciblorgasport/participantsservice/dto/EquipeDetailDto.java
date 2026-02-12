package com.ciblorgasport.participantsservice.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO equipe detaillee pour l'espace athlete.
 */
public class EquipeDetailDto {
    private Long id;
    private String nom;
    private String pays;
    private List<CoequipierDto> members = new ArrayList<>();

    public EquipeDetailDto() {
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

    public List<CoequipierDto> getMembers() {
        return members;
    }

    public void setMembers(List<CoequipierDto> members) {
        this.members = members;
    }
}
