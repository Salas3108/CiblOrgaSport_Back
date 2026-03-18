package com.ciblorgasport.analyticsservice.controller;

import com.ciblorgasport.analyticsservice.dto.DailyStatsResponse;
import com.ciblorgasport.analyticsservice.dto.EventLogRequest;
import com.ciblorgasport.analyticsservice.dto.WeeklyStatsResponse;
import com.ciblorgasport.analyticsservice.entity.EventLog;
import com.ciblorgasport.analyticsservice.service.DailyStatsService;
import com.ciblorgasport.analyticsservice.service.EventLogService;
import com.ciblorgasport.analyticsservice.service.WeeklyStatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final EventLogService eventLogService;
    private final DailyStatsService dailyStatsService;
    private final WeeklyStatsService weeklyStatsService;

    public AnalyticsController(EventLogService eventLogService,
                                DailyStatsService dailyStatsService,
                                WeeklyStatsService weeklyStatsService) {
        this.eventLogService = eventLogService;
        this.dailyStatsService = dailyStatsService;
        this.weeklyStatsService = weeklyStatsService;
    }

    /**
     * Reçoit les événements envoyés par le Gateway (clé interne).
     * Accès : gateway uniquement — permitAll() dans SecurityConfig.
     */
    @PostMapping("/events/track")
    public ResponseEntity<Void> track(@RequestBody EventLogRequest request) {
        eventLogService.saveAsync(request);
        return ResponseEntity.accepted().build();
    }

    /**
     * daily_stats d'une journée précise.
     */
    @GetMapping("/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DailyStatsResponse> getDailyStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dailyStatsService.findByDate(date)
                .map(s -> ResponseEntity.ok(DailyStatsResponse.from(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * daily_stats sur une période (pour les graphiques Metabase).
     */
    @GetMapping("/daily/range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DailyStatsResponse>> getDailyRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<DailyStatsResponse> list = dailyStatsService.findByRange(start, end)
                .stream()
                .map(DailyStatsResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    /**
     * weekly_stats d'une semaine précise.
     */
    @GetMapping("/weekly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WeeklyStatsResponse> getWeeklyStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return weeklyStatsService.findByWeekStart(weekStart)
                .map(s -> ResponseEntity.ok(WeeklyStatsResponse.from(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Toutes les weekly_stats disponibles.
     */
    @GetMapping("/weekly/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WeeklyStatsResponse>> getAllWeeklyStats() {
        List<WeeklyStatsResponse> list = weeklyStatsService.findAll()
                .stream()
                .map(WeeklyStatsResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    /**
     * Nombre d'événements par type pour aujourd'hui (temps réel).
     */
    @GetMapping("/events/today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getEventsToday() {
        return ResponseEntity.ok(eventLogService.getEventCountsForToday());
    }

    /**
     * Les 50 derniers événements bruts (activité en direct).
     */
    @GetMapping("/events/live")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventLog>> getLiveEvents() {
        return ResponseEntity.ok(eventLogService.getLiveEvents());
    }

    /**
     * Top 5 compétitions les plus vues sur une période.
     */
    @GetMapping("/top/competitions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getTopCompetitions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.plusDays(1).atStartOfDay();
        List<Object[]> rows = eventLogService.getTopCompetitions(startDt, endDt);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("competitionId", row[0]);
            entry.put("viewCount", row[1]);
            result.add(entry);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Force le recalcul des stats d'une journée (correction de données).
     */
    @PostMapping("/recalculate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> recalculate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        dailyStatsService.calculateForDate(date);
        return ResponseEntity.ok("Recalcul effectué pour " + date);
    }
}
