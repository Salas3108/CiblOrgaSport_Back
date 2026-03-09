package com.ciblorgasport.participantsservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "epreuve_athlete_assignments",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_epreuve_athlete",
        columnNames = {"epreuve_id", "athlete_id"}
    )
)
public class EpreuveAthleteAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "epreuve_id", nullable = false)
    private Long epreuveId;

    @Column(name = "athlete_id", nullable = false)
    private Long athleteId;

    public EpreuveAthleteAssignment() {
    }

    public EpreuveAthleteAssignment(Long epreuveId, Long athleteId) {
        this.epreuveId = epreuveId;
        this.athleteId = athleteId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEpreuveId() {
        return epreuveId;
    }

    public void setEpreuveId(Long epreuveId) {
        this.epreuveId = epreuveId;
    }

    public Long getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(Long athleteId) {
        this.athleteId = athleteId;
    }
}
