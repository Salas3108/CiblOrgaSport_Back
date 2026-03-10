package com.ciblorgasport.analyticsservice.service;

import com.ciblorgasport.analyticsservice.entity.DailyStats;
import com.ciblorgasport.analyticsservice.entity.WeeklyStats;
import com.ciblorgasport.analyticsservice.repository.DailyStatsRepository;
import com.ciblorgasport.analyticsservice.repository.WeeklyStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WeeklyStatsService {

    private static final Logger log = LoggerFactory.getLogger(WeeklyStatsService.class);

    private final WeeklyStatsRepository weeklyStatsRepository;
    private final DailyStatsRepository dailyStatsRepository;

    public WeeklyStatsService(WeeklyStatsRepository weeklyStatsRepository,
                               DailyStatsRepository dailyStatsRepository) {
        this.weeklyStatsRepository = weeklyStatsRepository;
        this.dailyStatsRepository = dailyStatsRepository;
    }

    /**
     * Calcule les weekly_stats pour la semaine qui contient weekStart (lundi).
     */
    @Transactional
    public void calculateForWeek(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        log.info("Calcul des weekly_stats pour la semaine du {} au {}", weekStart, weekEnd);

        List<DailyStats> dailyList = dailyStatsRepository.findByStatDateBetween(weekStart, weekEnd);

        WeeklyStats stats = weeklyStatsRepository.findByWeekStart(weekStart).orElse(new WeeklyStats());
        stats.setWeekStart(weekStart);
        stats.setWeekEnd(weekEnd);

        int totalConnections = dailyList.stream().mapToInt(d -> d.getTotalConnections() != null ? d.getTotalConnections() : 0).sum();
        int uniqueUsers = dailyList.stream().mapToInt(d -> d.getUniqueUsers() != null ? d.getUniqueUsers() : 0).sum();
        int totalNotifications = dailyList.stream().mapToInt(d -> d.getTotalNotificationsSent() != null ? d.getTotalNotificationsSent() : 0).sum();
        int totalSubscriptions = dailyList.stream().mapToInt(d -> d.getTotalSubscriptions() != null ? d.getTotalSubscriptions() : 0).sum();

        stats.setTotalConnections(totalConnections);
        stats.setUniqueUsers(uniqueUsers);
        stats.setTotalNotificationsSent(totalNotifications);
        stats.setTotalNewSubscriptions(totalSubscriptions);

        // Jour de pic
        dailyList.stream()
                .max((a, b) -> Integer.compare(
                        a.getTotalConnections() != null ? a.getTotalConnections() : 0,
                        b.getTotalConnections() != null ? b.getTotalConnections() : 0))
                .ifPresent(peak -> {
                    stats.setPeakDay(peak.getStatDate());
                    stats.setPeakConnections(peak.getTotalConnections() != null ? peak.getTotalConnections() : 0);
                });

        // Moyenne journalière
        stats.setAvgDailyConnections(dailyList.isEmpty() ? 0.0 : (double) totalConnections / dailyList.size());

        // Taux de croissance par rapport à la semaine précédente
        LocalDate previousWeekStart = weekStart.minusWeeks(1);
        weeklyStatsRepository.findByWeekStart(previousWeekStart).ifPresent(prev -> {
            int prevTotal = prev.getTotalConnections() != null ? prev.getTotalConnections() : 0;
            if (prevTotal > 0) {
                double growth = ((double)(totalConnections - prevTotal) / prevTotal) * 100.0;
                stats.setGrowthRatePercent(Math.round(growth * 100.0) / 100.0);
            }
        });

        stats.setCalculatedAt(LocalDateTime.now());
        weeklyStatsRepository.save(stats);
        log.info("weekly_stats calculées et sauvegardées pour la semaine du {}", weekStart);
    }

    public Optional<WeeklyStats> findByWeekStart(LocalDate weekStart) {
        return weeklyStatsRepository.findByWeekStart(weekStart);
    }

    public List<WeeklyStats> findAll() {
        return weeklyStatsRepository.findAllByOrderByWeekStartAsc();
    }

    /**
     * Retourne le lundi de la semaine courante.
     */
    public LocalDate currentWeekStart() {
        LocalDate today = LocalDate.now();
        return today.with(DayOfWeek.MONDAY);
    }
}
