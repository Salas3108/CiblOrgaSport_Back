package com.ciblorgasport.eventservice.dto;

import java.time.LocalDate;

public class EventDTO {
    private Long id;
    private String name;
    private LocalDate date;
    private Long lieuId;
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getLieuId() { return lieuId; }
    public void setLieuId(Long lieuId) { this.lieuId = lieuId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
