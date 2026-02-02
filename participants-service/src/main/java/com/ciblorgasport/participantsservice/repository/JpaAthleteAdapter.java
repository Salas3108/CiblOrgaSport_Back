package com.ciblorgasport.participantsservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ciblorgasport.participantsservice.model.Athlete;

/**
 * Adapter pour exposer JpaAthleteRepository via l'interface AthleteRepository.
 */
@Repository
public class JpaAthleteAdapter implements AthleteRepository {

    private final JpaAthleteRepository delegate;

    public JpaAthleteAdapter(JpaAthleteRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Athlete> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<Athlete> findById(Long id) {
        return delegate.findById(id);
    }

    @Override
    public Athlete save(Athlete athlete) {
        return delegate.save(athlete);
    }
}
