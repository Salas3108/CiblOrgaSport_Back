package com.ciblorgasport.participantsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ciblorgasport.participantsservice.model.Athlete;

@Repository
public interface JpaAthleteRepository extends JpaRepository<Athlete, Long> {
	java.util.Optional<Athlete> findByUsername(String username);
}
