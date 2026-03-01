package com.ciblorgasport.eventservice.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.ciblorgasport.eventservice.model.enums.CompetitionType;

@Entity
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonIgnoreProperties({"name", "dateDebut", "dateFin", "description", "paysHote"})
    private Event event;

    @Enumerated(EnumType.STRING)
    private CompetitionType discipline;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public CompetitionType getDiscipline() { return discipline; }
    public void setDiscipline(CompetitionType discipline) { this.discipline = discipline; }
    
}
