package com.ciblorgasport.participantsservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "epreuve_athletes",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_epreuve_athlete",
        columnNames = {"epreuve_id", "athlete_id"}
    )
)
public class EpreuveAthlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "epreuve_id", nullable = false)
    private Long epreuveId;

    @Column(name = "athlete_id", nullable = false)
    private Long athleteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_participation")
    private StatutParticipation statutParticipation = StatutParticipation.INSCRIT;

    @Column(name = "date_forfait")
    private LocalDateTime dateForfait;

    @Column(name = "details_performance", columnDefinition = "TEXT")
    private String detailsPerformance;

    public EpreuveAthlete() {}

    public EpreuveAthlete(Long epreuveId, Long athleteId) {
        this.epreuveId = epreuveId;
        this.athleteId = athleteId;
        this.statutParticipation = StatutParticipation.INSCRIT;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEpreuveId() { return epreuveId; }
    public void setEpreuveId(Long epreuveId) { this.epreuveId = epreuveId; }

    public Long getAthleteId() { return athleteId; }
    public void setAthleteId(Long athleteId) { this.athleteId = athleteId; }

    public StatutParticipation getStatutParticipation() { return statutParticipation; }
    public void setStatutParticipation(StatutParticipation statutParticipation) { this.statutParticipation = statutParticipation; }

    public LocalDateTime getDateForfait() { return dateForfait; }
    public void setDateForfait(LocalDateTime dateForfait) { this.dateForfait = dateForfait; }

    public String getDetailsPerformance() { return detailsPerformance; }
    public void setDetailsPerformance(String detailsPerformance) { this.detailsPerformance = detailsPerformance; }
}
