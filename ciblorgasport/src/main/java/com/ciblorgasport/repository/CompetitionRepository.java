package com.ciblorgasport.repository;

import com.ciblorgasport.entity.Competition;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface CompetitionRepository extends JpaRepository<Competition, UUID> {
    
    List<Competition> findByEventId(UUID eventId);
    
    @EntityGraph(attributePaths = {"event", "epreuves"})
    List<Competition> findAll();
    
    @Query("SELECT DISTINCT c FROM Competition c " +
           "LEFT JOIN FETCH c.event e " +
           "LEFT JOIN FETCH c.epreuves " +
           "ORDER BY c.name")
    List<Competition> findAllWithDetails();
}