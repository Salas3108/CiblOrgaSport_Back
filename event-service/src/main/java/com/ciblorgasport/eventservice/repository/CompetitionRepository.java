package com.ciblorgasport.eventservice.repository;

import com.ciblorgasport.eventservice.model.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
}
