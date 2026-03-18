package com.ciblorgasport.resultatsservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciblorgasport.resultatsservice.model.Resultat;

public interface ResultatRepository extends JpaRepository<Resultat, Long> {
    Optional<Resultat> findByEpreuveIdAndAthleteId(Long epreuveId, Long athleteId);
    Optional<Resultat> findByEpreuveIdAndEquipeId(Long epreuveId, Long equipeId);

    List<Resultat> findByEpreuveId(Long epreuveId);
    List<Resultat> findByEpreuveIdAndPublishedTrue(Long epreuveId);

    List<Resultat> findByAthleteId(Long athleteId);
    List<Resultat> findByAthleteIdAndPublishedTrue(Long athleteId);

    List<Resultat> findByEquipeId(Long equipeId);
    List<Resultat> findByEquipeIdAndPublishedTrue(Long equipeId);
}
