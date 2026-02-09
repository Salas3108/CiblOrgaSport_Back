package com.ciblorgasport.participantsservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ciblorgasport.participantsservice.model.Equipe;

/**
 * Adapter pour exposer JpaEquipeRepository via l'interface EquipeRepository.
 */
@Repository
public class JpaEquipeAdapter implements EquipeRepository {

    private final JpaEquipeRepository delegate;

    public JpaEquipeAdapter(JpaEquipeRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Equipe> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<Equipe> findById(Long id) {
        return delegate.findById(id);
    }

    @Override
    public Equipe save(Equipe equipe) {
        return delegate.save(equipe);
    }

    @Override
    public void deleteById(Long id) {
        delegate.deleteById(id);
    }
}
