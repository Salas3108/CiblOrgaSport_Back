package com.ciblorgasport.eventservice.dto;

import java.time.LocalDate;

public class EventDTO {
    private Long id;
    private String name;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String description;
    private String paysHote;

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
