package com.ciblorgasport.eventservice.model;
import com.ciblorgasport.eventservice.model.Event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.ciblorgasport.eventservice.model.enums.CompetitionType;

@Entity
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String type;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonIgnoreProperties({"lieuPrincipal"}) // Inclut event avec id, name, date mais sans lieuPrincipal
    private Event event;

    @Enumerated(EnumType.STRING)
    private CompetitionType typeCompetition;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
	public LocalDate getDateDebut() {
		return dateDebut;
	}
	public void setDateDebut(LocalDate dateDebut) {
		this.dateDebut = dateDebut;
	}
	public LocalDate getDateFin() {
		return dateFin;
	}
	public void setDateFin(LocalDate dateFin) {
		this.dateFin = dateFin;
	}
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public CompetitionType getTypeCompetition() { return typeCompetition; }
    public void setTypeCompetition(CompetitionType typeCompetition) { this.typeCompetition = typeCompetition; }
    
}
