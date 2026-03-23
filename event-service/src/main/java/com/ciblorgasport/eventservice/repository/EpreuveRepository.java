package com.ciblorgasport.eventservice.repository;

import com.ciblorgasport.eventservice.model.Epreuve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface EpreuveRepository extends JpaRepository<Epreuve, Long> {

    @Query(value = "SELECT e.* FROM epreuve e WHERE e.id IN " +
                   "(SELECT epreuve_id FROM epreuve_athletes WHERE athlete_id = :athleteId)",
           nativeQuery = true)
    List<Epreuve> findByAthleteId(@Param("athleteId") Long athleteId);

    List<Epreuve> findByDateHeureBetween(LocalDateTime from, LocalDateTime to);
}
