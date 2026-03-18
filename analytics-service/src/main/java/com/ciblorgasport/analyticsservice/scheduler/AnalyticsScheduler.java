package com.ciblorgasport.analyticsservice.scheduler;

import com.ciblorgasport.analyticsservice.service.DailyStatsService;
import com.ciblorgasport.analyticsservice.service.WeeklyStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
public class AnalyticsScheduler {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsScheduler.class);

    private final DailyStatsService dailyStatsService;
    private final WeeklyStatsService weeklyStatsService;

    public AnalyticsScheduler(DailyStatsService dailyStatsService,
                               WeeklyStatsService weeklyStatsService) {
        this.dailyStatsService = dailyStatsService;
        this.weeklyStatsService = weeklyStatsService;
    }

    /**
     * Calcul des daily_stats chaque nuit à 00h01 pour la journée J-1.
     * En cas d'échec, tente un recalcul sur J-2 également.
     */
    @Scheduled(cron = "0 1 0 * * *")
    public void calculateDailyStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("[Scheduler] Début du calcul daily_stats pour {}", yesterday);
        try {
            dailyStatsService.calculateForDate(yesterday);
            log.info("[Scheduler] daily_stats calculées avec succès pour {}", yesterday);
        } catch (Exception e) {
            log.error("[Scheduler] Échec du calcul daily_stats pour {} : {}", yesterday, e.getMessage(), e);
            // Tentative de rattrapage pour J-2
            try {
                LocalDate twoDaysAgo = yesterday.minusDays(1);
                log.info("[Scheduler] Tentative de rattrapage pour {}", twoDaysAgo);
                dailyStatsService.calculateForDate(twoDaysAgo);
            } catch (Exception ex) {
                log.error("[Scheduler] Échec du rattrapage : {}", ex.getMessage(), ex);
            }
        }
    }

    /**
     * Calcul des weekly_stats chaque lundi à 00h05.
     */
    @Scheduled(cron = "0 5 0 * * MON")
    public void calculateWeeklyStats() {
        // La semaine vient de se terminer (lundi = début de la nouvelle semaine)
        // On calcule pour la semaine précédente : lundi dernier → dimanche dernier
        LocalDate previousMonday = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
        log.info("[Scheduler] Début du calcul weekly_stats pour la semaine du {}", previousMonday);
        try {
            weeklyStatsService.calculateForWeek(previousMonday);
            log.info("[Scheduler] weekly_stats calculées avec succès pour la semaine du {}", previousMonday);
        } catch (Exception e) {
            log.error("[Scheduler] Échec du calcul weekly_stats pour la semaine du {} : {}", previousMonday, e.getMessage(), e);
        }
    }
}
