package com.ciblorgasport.repository;

import com.ciblorgasport.entity.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AbonnementRepository extends JpaRepository<Abonnement, UUID> {
    // Note: primary key (id) remains UUID; only competitionId changed to Long
    
    List<Abonnement> findByUserId(Long userId);
    
    Optional<Abonnement> findByUserIdAndCompetitionId(Long userId, Long competitionId);
    
    boolean existsByUserIdAndCompetitionId(Long userId, Long competitionId);
    
    List<Abonnement> findByCompetitionId(Long competitionId);

    List<Abonnement> findByCompetitionIdAndNotificationsActivesTrue(Long competitionId);

    long countByCompetitionId(Long competitionId);
}
