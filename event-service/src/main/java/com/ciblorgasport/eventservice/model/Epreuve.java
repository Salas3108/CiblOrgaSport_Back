package com.ciblorgasport.eventservice.model;
import java.time.LocalDate;
import java.time.LocalTime;

import com.ciblorgasport.eventservice.model.Competition;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import com.ciblorgasport.eventservice.model.Lieu;

@Entity
public class Epreuve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String description;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    @ManyToOne
    @JoinColumn(name = "competition_id")
    @JsonIgnoreProperties({"event"}) // Inclut competition sans l'event pour éviter trop de niveaux
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "lieu_id")
    private Lieu lieu;
    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public Lieu getLieu() { return lieu; }
    public void setLieu(Lieu lieu) { this.lieu = lieu; }
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalTime getHeureDebut() {
		return heureDebut;
	}
	public void setHeureDebut(LocalTime heureDebut) {
		this.heureDebut = heureDebut;
	}
	public LocalTime getHeureFin() {
		return heureFin;
	}
	public void setHeureFin(LocalTime heureFin) {
		this.heureFin = heureFin;
	}
    
}
