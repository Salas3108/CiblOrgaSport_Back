package com.ciblorgasport.analyticsservice.dto;

import com.ciblorgasport.analyticsservice.entity.WeeklyStats;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class WeeklyStatsResponse {

    private Long id;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private Integer totalConnections;
    private Integer uniqueUsers;
    private LocalDate peakDay;
    private Integer peakConnections;
    private Long topCompetitionId;
    private Integer topCompetitionViews;
    private Integer totalNotificationsSent;
    private Integer totalNewSubscriptions;
    private Double avgDailyConnections;
    private Double growthRatePercent;
    private LocalDateTime calculatedAt;

    public static WeeklyStatsResponse from(WeeklyStats s) {
        WeeklyStatsResponse r = new WeeklyStatsResponse();
        r.id = s.getId();
        r.weekStart = s.getWeekStart();
        r.weekEnd = s.getWeekEnd();
        r.totalConnections = s.getTotalConnections();
        r.uniqueUsers = s.getUniqueUsers();
        r.peakDay = s.getPeakDay();
        r.peakConnections = s.getPeakConnections();
        r.topCompetitionId = s.getTopCompetitionId();
        r.topCompetitionViews = s.getTopCompetitionViews();
        r.totalNotificationsSent = s.getTotalNotificationsSent();
        r.totalNewSubscriptions = s.getTotalNewSubscriptions();
        r.avgDailyConnections = s.getAvgDailyConnections();
        r.growthRatePercent = s.getGrowthRatePercent();
        r.calculatedAt = s.getCalculatedAt();
        return r;
    }

    public Long getId() { return id; }
    public LocalDate getWeekStart() { return weekStart; }
    public LocalDate getWeekEnd() { return weekEnd; }
    public Integer getTotalConnections() { return totalConnections; }
    public Integer getUniqueUsers() { return uniqueUsers; }
    public LocalDate getPeakDay() { return peakDay; }
    public Integer getPeakConnections() { return peakConnections; }
    public Long getTopCompetitionId() { return topCompetitionId; }
    public Integer getTopCompetitionViews() { return topCompetitionViews; }
    public Integer getTotalNotificationsSent() { return totalNotificationsSent; }
    public Integer getTotalNewSubscriptions() { return totalNewSubscriptions; }
    public Double getAvgDailyConnections() { return avgDailyConnections; }
    public Double getGrowthRatePercent() { return growthRatePercent; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
}
