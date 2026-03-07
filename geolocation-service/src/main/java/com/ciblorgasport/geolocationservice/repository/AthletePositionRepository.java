package com.ciblorgasport.geolocationservice.repository;

import com.ciblorgasport.geolocationservice.entity.AthletePosition;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
