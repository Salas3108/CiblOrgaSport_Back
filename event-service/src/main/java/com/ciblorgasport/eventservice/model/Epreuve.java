package com.ciblorgasport.eventservice.model;
import java.time.LocalDateTime;

import com.ciblorgasport.eventservice.model.Competition;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import com.ciblorgasport.eventservice.model.Lieu;

import com.ciblorgasport.eventservice.model.enums.TypeEpreuve;
import com.ciblorgasport.eventservice.model.enums.GenreEpreuve;
import com.ciblorgasport.eventservice.model.enums.NiveauEpreuve;
import com.ciblorgasport.eventservice.model.enums.StatutEpreuve;

import java.util.Set;
import java.util.HashSet;

@Entity
public class Epreuve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String description;
    private LocalDateTime dateHeure;
    private Integer dureeMinutes;
    @ManyToOne
    @JoinColumn(name = "competition_id")
    @JsonIgnoreProperties({"event"}) // Inclut competition sans l'event pour éviter trop de niveaux
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "lieu_id")
    private Lieu lieu;

    @Enumerated(EnumType.STRING)
    private TypeEpreuve typeEpreuve;

    @Enumerated(EnumType.STRING)
    private GenreEpreuve genreEpreuve;

    @Enumerated(EnumType.STRING)
    private NiveauEpreuve niveauEpreuve;

    @Enumerated(EnumType.STRING)
    private StatutEpreuve statut = StatutEpreuve.PLANIFIE;

    @Column(name = "equipe_id")
    private Long equipeId;

    // participants (IDs) -> permet d'ajouter des athlètes par leur id sans dépendre d'un entity Athlete
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "epreuve_athletes", joinColumns = @JoinColumn(name = "epreuve_id"))
    @Column(name = "athlete_id")
    private Set<Long> athleteIds = new HashSet<>();

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
    public TypeEpreuve getTypeEpreuve() { return typeEpreuve; }
    public void setTypeEpreuve(TypeEpreuve typeEpreuve) { this.typeEpreuve = typeEpreuve; }
    public GenreEpreuve getGenreEpreuve() { return genreEpreuve; }
    public void setGenreEpreuve(GenreEpreuve genreEpreuve) { this.genreEpreuve = genreEpreuve; }
    public NiveauEpreuve getNiveauEpreuve() { return niveauEpreuve; }
    public void setNiveauEpreuve(NiveauEpreuve niveauEpreuve) { this.niveauEpreuve = niveauEpreuve; }
    public StatutEpreuve getStatut() { return statut; }
    public void setStatut(StatutEpreuve statut) { this.statut = statut; }
    public Long getEquipeId() { return equipeId; }
    public void setEquipeId(Long equipeId) { this.equipeId = equipeId; }
    public Set<Long> getAthleteIds() { return athleteIds; }
    public void setAthleteIds(Set<Long> athleteIds) { this.athleteIds = athleteIds; }
    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
    public Integer getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(Integer dureeMinutes) { this.dureeMinutes = dureeMinutes; }
}
