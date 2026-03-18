package com.ciblorgasport.eventservice.repository;

import com.ciblorgasport.eventservice.model.Epreuve;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface EpreuveRepository extends JpaRepository<Epreuve, Long> {
	List<Epreuve> findByAthleteIdsContains(Long athleteId);

	List<Epreuve> findByDateHeureBetween(LocalDateTime from, LocalDateTime to);
}
