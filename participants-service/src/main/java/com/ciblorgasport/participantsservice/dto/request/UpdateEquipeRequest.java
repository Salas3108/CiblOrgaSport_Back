package com.ciblorgasport.participantsservice.dto.request;

/**
 * Requete Commissaire : mettre a jour une equipe.
 */
public class UpdateEquipeRequest {
    private String nom;
    private String pays;

    public UpdateEquipeRequest() {
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
