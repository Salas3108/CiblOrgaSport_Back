package com.ciblorgasport.eventservice.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.ciblorgasport.eventservice.model.enums.TypeEpreuve;
import com.ciblorgasport.eventservice.model.enums.GenreEpreuve;
import com.ciblorgasport.eventservice.model.enums.NiveauEpreuve;

public class EpreuveDTO {
    private Long id;

    @NotBlank
    private String nom;

    private String description;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;

    @NotNull
    private TypeEpreuve typeEpreuve;

    @NotNull
    private GenreEpreuve genreEpreuve;

    @NotNull
    private NiveauEpreuve niveauEpreuve;

    // participant ids (athletes or teams)
    private Set<Long> participantIds;

    // optional: when creating/updating in non-admin path you can specify competition id
    private Long competitionId;

    private Set<Long> athleteIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public TypeEpreuve getTypeEpreuve() { return typeEpreuve; }
    public void setTypeEpreuve(TypeEpreuve typeEpreuve) { this.typeEpreuve = typeEpreuve; }

    public GenreEpreuve getGenreEpreuve() { return genreEpreuve; }
    public void setGenreEpreuve(GenreEpreuve genreEpreuve) { this.genreEpreuve = genreEpreuve; }

    public NiveauEpreuve getNiveauEpreuve() { return niveauEpreuve; }
    public void setNiveauEpreuve(NiveauEpreuve niveauEpreuve) { this.niveauEpreuve = niveauEpreuve; }

    public Set<Long> getParticipantIds() { return participantIds; }
    public void setParticipantIds(Set<Long> participantIds) { this.participantIds = participantIds; }

    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }

    public Set<Long> getAthleteIds() {
        return athleteIds;
    }

    public void setAthleteIds(Set<Long> athleteIds) {
        this.athleteIds = athleteIds;
    }
}
