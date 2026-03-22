package com.ciblorgasport.participantsservice.dto;

import java.time.LocalDate;

import com.ciblorgasport.participantsservice.model.Sexe;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * DTO Athlète : correspond au JSON attendu par le front.
 */
public class AthleteDto {
    private Long id;
    private String username;
    private String nom;
    private String prenom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;

    private String pays;
    private boolean valide;
    private Sexe sexe;
    private AthleteDocsDto docs;
    private String observation;

    private Long equipeId;
    private String equipeNom;

    // Champ utilisé quand un commissaire refuse/justifie (optionnel côté front)
    private String motifRefus;

    public AthleteDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public AthleteDocsDto getDocs() {
        return docs;
    }

    public void setDocs(AthleteDocsDto docs) {
        this.docs = docs;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public Long getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(Long equipeId) {
        this.equipeId = equipeId;
    }

    public String getEquipeNom() {
        return equipeNom;
    }

    public void setEquipeNom(String equipeNom) {
        this.equipeNom = equipeNom;
    }

    public String getMotifRefus() {
        return motifRefus;
    }

    public void setMotifRefus(String motifRefus) {
        this.motifRefus = motifRefus;
    }
}
