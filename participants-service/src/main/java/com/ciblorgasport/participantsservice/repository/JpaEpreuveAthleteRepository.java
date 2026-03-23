package com.ciblorgasport.participantsservice.repository;

import com.ciblorgasport.participantsservice.model.EpreuveAthlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaEpreuveAthleteRepository extends JpaRepository<EpreuveAthlete, Long> {
    List<EpreuveAthlete> findByEpreuveId(Long epreuveId);
    List<EpreuveAthlete> findByEpreuveIdIn(List<Long> epreuveIds);
    Optional<EpreuveAthlete> findByAthleteIdAndEpreuveId(Long athleteId, Long epreuveId);
    boolean existsByEpreuveIdAndAthleteId(Long epreuveId, Long athleteId);
}
