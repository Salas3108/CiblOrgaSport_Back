package com.ciblorgasport.eventservice.dto;

import java.time.LocalDate;

public class EventDTO {
    private Long id;
    private String name;
    private Long lieuPrincipalId;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getLieuPrincipalId() { return lieuPrincipalId; }
    public void setLieuPrincipalId(Long lieuPrincipalId) { this.lieuPrincipalId = lieuPrincipalId; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}
