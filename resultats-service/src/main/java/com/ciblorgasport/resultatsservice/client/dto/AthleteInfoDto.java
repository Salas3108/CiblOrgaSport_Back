package com.ciblorgasport.resultatsservice.client.dto;

/**
 * Miroir local des informations d'un athlète issues du participants-service.
 */
public class AthleteInfoDto {

    private Long id;
    private String nom;
    private String prenom;
    private String pays;

    public AthleteInfoDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
}
