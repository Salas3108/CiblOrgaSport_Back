package com.ciblorgasport.participantsservice.repository;

import java.util.List;
import java.util.Optional;

import com.ciblorgasport.participantsservice.model.Equipe;

public interface EquipeRepository {
    List<Equipe> findAll();
    Optional<Equipe> findById(Long id);
    Equipe save(Equipe equipe);
    void deleteById(Long id);
}
