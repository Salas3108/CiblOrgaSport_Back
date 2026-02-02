package com.ciblorgasport.participantsservice.repository;

import java.util.List;
import java.util.Optional;

import com.ciblorgasport.participantsservice.model.Athlete;

public interface AthleteRepository {
    List<Athlete> findAll();
    Optional<Athlete> findById(Long id);
    Athlete save(Athlete athlete);
}
