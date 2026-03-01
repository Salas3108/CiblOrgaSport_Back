package com.ciblorgasport.eventservice.model;

import jakarta.persistence.*;

@Entity
public class Lieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String adresse;
    private String ville;
    private String codePostal;
    private String pays;
    private Integer capaciteSpectateurs;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public Integer getCapaciteSpectateurs() { return capaciteSpectateurs; }
    public void setCapaciteSpectateurs(Integer capaciteSpectateurs) { this.capaciteSpectateurs = capaciteSpectateurs; }
}
