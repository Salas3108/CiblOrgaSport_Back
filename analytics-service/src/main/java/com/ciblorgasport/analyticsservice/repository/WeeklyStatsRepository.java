package com.ciblorgasport.analyticsservice.repository;

import com.ciblorgasport.analyticsservice.entity.WeeklyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyStatsRepository extends JpaRepository<WeeklyStats, Long> {

    Optional<WeeklyStats> findByWeekStart(LocalDate weekStart);

    List<WeeklyStats> findAllByOrderByWeekStartAsc();

    // Semaine précédente (pour le calcul du taux de croissance)
    Optional<WeeklyStats> findByWeekStartBefore(LocalDate weekStart);
}
