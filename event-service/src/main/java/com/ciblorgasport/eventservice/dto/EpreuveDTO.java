package com.ciblorgasport.eventservice.dto;

import java.time.LocalDateTime;
import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ciblorgasport.eventservice.model.enums.TypeEpreuve;
import com.ciblorgasport.eventservice.model.enums.GenreEpreuve;
import com.ciblorgasport.eventservice.model.enums.NiveauEpreuve;
import com.ciblorgasport.eventservice.model.enums.StatutEpreuve;
import com.ciblorgasport.eventservice.dto.deserializer.FlexibleLongDeserializer;

public class EpreuveDTO {
    private Long id;

    @NotBlank
    private String nom;

    private String description;
    @NotNull
    private LocalDateTime dateHeure;

    @NotNull
    private Integer dureeMinutes;

    private StatutEpreuve statut;

    @NotNull
    private TypeEpreuve typeEpreuve;

    @NotNull
    private GenreEpreuve genreEpreuve;

    @NotNull
    private NiveauEpreuve niveauEpreuve;

    // remplacez :
    // private Set<Long> participantIds;
    // par :
    @JsonDeserialize(contentUsing = FlexibleLongDeserializer.class)
    private Set<Long> equipeIds;

    // optional: when creating/updating in non-admin path you can specify competition id
    @JsonDeserialize(using = FlexibleLongDeserializer.class)
    private Long competitionId;

    // optional: specify epreuve location
    @JsonDeserialize(using = FlexibleLongDeserializer.class)
    private Long lieuId;

    @JsonDeserialize(contentUsing = FlexibleLongDeserializer.class)
    private Set<Long> athleteIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public Integer getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(Integer dureeMinutes) { this.dureeMinutes = dureeMinutes; }

    public StatutEpreuve getStatut() { return statut; }
    public void setStatut(StatutEpreuve statut) { this.statut = statut; }

    public TypeEpreuve getTypeEpreuve() { return typeEpreuve; }
    public void setTypeEpreuve(TypeEpreuve typeEpreuve) { this.typeEpreuve = typeEpreuve; }

    public GenreEpreuve getGenreEpreuve() { return genreEpreuve; }
    public void setGenreEpreuve(GenreEpreuve genreEpreuve) { this.genreEpreuve = genreEpreuve; }

    public NiveauEpreuve getNiveauEpreuve() { return niveauEpreuve; }
    public void setNiveauEpreuve(NiveauEpreuve niveauEpreuve) { this.niveauEpreuve = niveauEpreuve; }

    public Set<Long> getEquipeIds() { return equipeIds; }
    public void setEquipeIds(Set<Long> equipeIds) { this.equipeIds = equipeIds; }

    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }

    public Long getLieuId() { return lieuId; }
    public void setLieuId(Long lieuId) { this.lieuId = lieuId; }

    public Set<Long> getAthleteIds() {
        return athleteIds;
    }

    public void setAthleteIds(Set<Long> athleteIds) {
        this.athleteIds = athleteIds;
    }
}
