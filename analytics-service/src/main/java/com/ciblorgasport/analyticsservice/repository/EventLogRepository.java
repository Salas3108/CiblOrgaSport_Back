package com.ciblorgasport.analyticsservice.repository;

import com.ciblorgasport.analyticsservice.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {

    // Événements d'une journée
    @Query("SELECT e FROM EventLog e WHERE e.timestamp >= :start AND e.timestamp < :end")
    List<EventLog> findByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Nombre total de LOGIN pour une journée
    @Query("SELECT COUNT(e) FROM EventLog e WHERE e.eventType = 'USER_LOGIN' AND e.timestamp >= :start AND e.timestamp < :end")
    Long countLoginsByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Nombre de LOGIN par rôle pour une journée
    @Query("SELECT COUNT(e) FROM EventLog e WHERE e.eventType = 'USER_LOGIN' AND e.userRole = :role AND e.timestamp >= :start AND e.timestamp < :end")
    Long countLoginsByRoleAndPeriod(@Param("role") String role, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Utilisateurs uniques connectés dans une période
    @Query("SELECT COUNT(DISTINCT e.userId) FROM EventLog e WHERE e.eventType = 'USER_LOGIN' AND e.timestamp >= :start AND e.timestamp < :end AND e.userId IS NOT NULL")
    Long countUniqueUsersByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Nombre d'événements d'un type précis dans une période
    @Query("SELECT COUNT(e) FROM EventLog e WHERE e.eventType = :eventType AND e.timestamp >= :start AND e.timestamp < :end")
    Long countByEventTypeAndPeriod(@Param("eventType") String eventType, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Temps de réponse moyen dans une période
    @Query("SELECT COALESCE(AVG(e.durationMs), 0) FROM EventLog e WHERE e.timestamp >= :start AND e.timestamp < :end AND e.durationMs IS NOT NULL")
    Double avgResponseTimeByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 50 derniers événements bruts (pour le live feed)
    List<EventLog> findTop50ByOrderByTimestampDesc();

    // Nombre d'événements par type pour aujourd'hui (utilisé par /events/today)
    @Query("SELECT e.eventType, COUNT(e) FROM EventLog e WHERE e.timestamp >= :start AND e.timestamp < :end GROUP BY e.eventType")
    List<Object[]> countByEventTypeGroupedForPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Top compétitions vues sur une période (endpoint /metadata contient competitionId)
    @Query(value = """
            SELECT CAST(metadata->>'competitionId' AS BIGINT) AS competition_id,
                   COUNT(*) AS view_count
            FROM event_log
            WHERE event_type = 'COMPETITION_VIEW'
              AND timestamp >= :start AND timestamp < :end
              AND metadata IS NOT NULL
              AND jsonb_exists(metadata::jsonb, 'competitionId')
            GROUP BY competition_id
            ORDER BY view_count DESC
            LIMIT 5
            """, nativeQuery = true)
    List<Object[]> findTopCompetitions(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
