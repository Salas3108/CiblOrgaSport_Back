package com.ciblorgasport.participantsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ciblorgasport.participantsservice.model.Equipe;

@Repository
public interface JpaEquipeRepository extends JpaRepository<Equipe, Long> {
}
