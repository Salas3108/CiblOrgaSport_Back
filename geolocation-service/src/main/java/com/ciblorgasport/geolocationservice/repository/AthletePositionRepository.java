package com.ciblorgasport.geolocationservice.repository;

import com.ciblorgasport.geolocationservice.entity.AthletePosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AthletePositionRepository extends JpaRepository<AthletePosition, Long> {

    Optional<AthletePosition> findTopByAthleteIdOrderByTimestampDesc(Long athleteId);

    List<AthletePosition> findByAthleteIdAndTimestampBetweenOrderByTimestampAsc(
            Long athleteId, LocalDateTime dateDebut, LocalDateTime dateFin);

    void deleteAllByAthleteId(Long athleteId);

    /**
     * Retourne la dernière position connue de chaque athlète pour une épreuve donnée.
     * Utilise une sous-requête pour ne garder que la position la plus récente par athlète.
     */
    @Query("""
            SELECT ap FROM AthletePosition ap
            WHERE ap.epreuveId = :epreuveId
              AND ap.timestamp = (
                  SELECT MAX(ap2.timestamp)
                  FROM AthletePosition ap2
                  WHERE ap2.athleteId = ap.athleteId
                    AND ap2.epreuveId = :epreuveId
              )
            ORDER BY ap.athleteId
            """)
    List<AthletePosition> findLatestPositionsPerAthleteByEpreuveId(@Param("epreuveId") Long epreuveId);
}
