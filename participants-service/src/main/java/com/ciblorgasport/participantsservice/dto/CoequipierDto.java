package com.ciblorgasport.participantsservice.dto;

/**
 * DTO coequipier pour l'equipe d'un athlete.
 */
public class CoequipierDto {
    private Long id;
    private String nom;
    private String prenom;
    private String pays;
    private String username;

    public CoequipierDto() {
    }

    public CoequipierDto(Long id, String nom, String prenom, String pays) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pays = pays;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
