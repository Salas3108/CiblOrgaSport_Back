package com.ciblorgasport.entity;

import jakarta.persistence.*;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "epreuves")
public class Epreuve {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String category;
    private Integer maxParticipants;

    @ManyToOne
    @JoinColumn(name = "competition_id", nullable = false)
    @JsonIgnore
    private Competition competition;

    public Epreuve() {}

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
    @JsonProperty("competitionId")
    public UUID getCompetitionId() {
        return competition != null ? competition.getId() : null;
    }
}
