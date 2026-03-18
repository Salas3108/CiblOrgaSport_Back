package com.ciblorgasport.eventservice.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String description;
    private String paysHote;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPaysHote() { return paysHote; }
    public void setPaysHote(String paysHote) { this.paysHote = paysHote; }
}
