package com.ciblorgasport.eventservice.model;
import com.ciblorgasport.eventservice.model.Event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDate date;
    private String type;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonIgnoreProperties({"lieuPrincipal"}) // Inclut event avec id, name, date mais sans lieuPrincipal
    private Event event;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}
