package com.ciblorgasport.repository;

import com.ciblorgasport.entity.Epreuve;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EpreuveRepository extends JpaRepository<Epreuve,UUID>  {}
