package com.ciblorgasport.notificationsservice.repository;

import com.ciblorgasport.notificationsservice.model.Abonnement;
import com.ciblorgasport.notificationsservice.model.AbonnementId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbonnementRepository extends JpaRepository<Abonnement, AbonnementId> {

    List<Abonnement> findByIdIdCompetitionAndPreferenceNotifTrue(Long idCompetition);
}
