package com.ciblorgasport.analyticsservice.repository;

import com.ciblorgasport.analyticsservice.entity.DailyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {

    Optional<DailyStats> findByStatDate(LocalDate statDate);

    List<DailyStats> findByStatDateBetweenOrderByStatDateAsc(LocalDate start, LocalDate end);

    // Agrégation des daily_stats pour une semaine
    @Query("SELECT SUM(d.totalConnections) FROM DailyStats d WHERE d.statDate >= :start AND d.statDate <= :end")
    Long sumConnectionsByPeriod(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT SUM(d.uniqueUsers) FROM DailyStats d WHERE d.statDate >= :start AND d.statDate <= :end")
    Long sumUniqueUsersByPeriod(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT SUM(d.totalNotificationsSent) FROM DailyStats d WHERE d.statDate >= :start AND d.statDate <= :end")
    Long sumNotificationsByPeriod(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT SUM(d.totalSubscriptions) FROM DailyStats d WHERE d.statDate >= :start AND d.statDate <= :end")
    Long sumSubscriptionsByPeriod(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // Jour de pic de connexions dans une semaine
    @Query("SELECT d FROM DailyStats d WHERE d.statDate >= :start AND d.statDate <= :end ORDER BY d.totalConnections DESC LIMIT 1")
    Optional<DailyStats> findPeakDayByPeriod(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // Compétition la plus vue sur une période (via event_log — délégué au EventLogRepository)
    List<DailyStats> findByStatDateBetween(LocalDate start, LocalDate end);
}
