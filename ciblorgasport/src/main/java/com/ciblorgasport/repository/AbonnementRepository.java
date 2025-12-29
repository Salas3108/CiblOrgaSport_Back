package com.ciblorgasport.repository;

import com.ciblorgasport.entity.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AbonnementRepository extends JpaRepository<Abonnement, UUID> {
    
    List<Abonnement> findByUserId(Long userId);
    
    Optional<Abonnement> findByUserIdAndCompetitionId(Long userId, UUID competitionId);
    
    boolean existsByUserIdAndCompetitionId(Long userId, UUID competitionId);
    
    List<Abonnement> findByCompetitionId(UUID competitionId);
    
    long countByCompetitionId(UUID competitionId);
}