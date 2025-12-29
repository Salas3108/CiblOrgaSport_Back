package com.ciblorgasport.repository;

import com.ciblorgasport.entity.Competition;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, UUID> {}


