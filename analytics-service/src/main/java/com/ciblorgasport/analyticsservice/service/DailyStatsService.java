package com.ciblorgasport.analyticsservice.service;

import com.ciblorgasport.analyticsservice.entity.DailyStats;
import com.ciblorgasport.analyticsservice.repository.DailyStatsRepository;
import com.ciblorgasport.analyticsservice.repository.EventLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DailyStatsService {

    private static final Logger log = LoggerFactory.getLogger(DailyStatsService.class);

    private final DailyStatsRepository dailyStatsRepository;
    private final EventLogRepository eventLogRepository;

    public DailyStatsService(DailyStatsRepository dailyStatsRepository,
                              EventLogRepository eventLogRepository) {
        this.dailyStatsRepository = dailyStatsRepository;
        this.eventLogRepository = eventLogRepository;
    }

    /**
     * Calcule et persiste les daily_stats pour la date donnée.
     * Utilise INSERT OR UPDATE (findByStatDate + save).
     */
    @Transactional
    public void calculateForDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        log.info("Calcul des daily_stats pour {}", date);

        DailyStats stats = dailyStatsRepository.findByStatDate(date).orElse(new DailyStats());
        stats.setStatDate(date);

        // Connexions totales
        Long totalLogins = eventLogRepository.countLoginsByPeriod(start, end);
        stats.setTotalConnections(totalLogins != null ? totalLogins.intValue() : 0);

        // Utilisateurs uniques
        Long uniqueUsers = eventLogRepository.countUniqueUsersByPeriod(start, end);
        stats.setUniqueUsers(uniqueUsers != null ? uniqueUsers.intValue() : 0);

        // Connexions par rôle
        stats.setConnectionsAthletes(toInt(eventLogRepository.countLoginsByRoleAndPeriod("ATHLETE", start, end)));
        stats.setConnectionsSpectateurs(toInt(eventLogRepository.countLoginsByRoleAndPeriod("USER", start, end)));
        stats.setConnectionsCommissaires(toInt(eventLogRepository.countLoginsByRoleAndPeriod("COMMISSAIRE", start, end)));
        stats.setConnectionsVolontaires(toInt(eventLogRepository.countLoginsByRoleAndPeriod("VOLONTAIRE", start, end)));
        stats.setConnectionsAdmins(toInt(eventLogRepository.countLoginsByRoleAndPeriod("ADMIN", start, end)));

        // Vues
        stats.setTotalPageViews(toInt(eventLogRepository.countByEventTypeAndPeriod("PAGE_VIEW", start, end)));
        stats.setTotalCompetitionViews(toInt(eventLogRepository.countByEventTypeAndPeriod("COMPETITION_VIEW", start, end)));
        stats.setTotalResultViews(toInt(eventLogRepository.countByEventTypeAndPeriod("RESULT_VIEW", start, end)));

        // Notifications
        stats.setTotalNotificationsSent(toInt(eventLogRepository.countByEventTypeAndPeriod("NOTIFICATION_SENT", start, end)));
        stats.setTotalSubscriptions(toInt(eventLogRepository.countByEventTypeAndPeriod("NOTIFICATION_SUBSCRIBED", start, end)));

        // Incidents
        stats.setTotalIncidents(toInt(eventLogRepository.countByEventTypeAndPeriod("INCIDENT_DECLARED", start, end)));

        // Temps de réponse moyen
        Double avgResponse = eventLogRepository.avgResponseTimeByPeriod(start, end);
        stats.setAvgResponseTimeMs(avgResponse != null ? avgResponse.longValue() : 0L);

        stats.setCalculatedAt(LocalDateTime.now());
        dailyStatsRepository.save(stats);
        log.info("daily_stats calculées et sauvegardées pour {}", date);
    }

    public Optional<DailyStats> findByDate(LocalDate date) {
        return dailyStatsRepository.findByStatDate(date);
    }

    public List<DailyStats> findByRange(LocalDate start, LocalDate end) {
        return dailyStatsRepository.findByStatDateBetweenOrderByStatDateAsc(start, end);
    }

    private int toInt(Long value) {
        return value != null ? value.intValue() : 0;
    }
}
