package com.ciblorgasport.participantsservice.dto.request;

/**
 * Requete Commissaire : creer une equipe.
 */
public class CreateEquipeRequest {
    private String nom;
    private String pays;

    public CreateEquipeRequest() {
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
}
