package com.ciblorgasport.participantsservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ciblorgasport.participantsservice.model.EpreuveAthleteAssignment;

@Repository
public interface JpaEpreuveAthleteAssignmentRepository extends JpaRepository<EpreuveAthleteAssignment, Long> {
    List<EpreuveAthleteAssignment> findByEpreuveId(Long epreuveId);
    List<EpreuveAthleteAssignment> findByEpreuveIdIn(List<Long> epreuveIds);
    boolean existsByEpreuveIdAndAthleteId(Long epreuveId, Long athleteId);
}
