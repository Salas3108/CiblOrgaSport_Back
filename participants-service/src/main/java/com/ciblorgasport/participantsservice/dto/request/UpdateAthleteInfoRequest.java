package com.ciblorgasport.participantsservice.dto.request;

import java.time.LocalDate;

import com.ciblorgasport.participantsservice.model.Sexe;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Requête Athlète : compléter / mettre à jour ses informations.
 */
public class UpdateAthleteInfoRequest {
    private String nom;
    private String prenom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;

    private String pays;
    private Sexe sexe;

    public UpdateAthleteInfoRequest() {
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

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }
}
