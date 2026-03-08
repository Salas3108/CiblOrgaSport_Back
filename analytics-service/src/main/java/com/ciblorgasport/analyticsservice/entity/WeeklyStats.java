package com.ciblorgasport.analyticsservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weekly_stats")
public class WeeklyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "week_start", unique = true, nullable = false)
    private LocalDate weekStart;

    @Column(name = "week_end")
    private LocalDate weekEnd;

    @Column(name = "total_connections")
    private Integer totalConnections = 0;

    @Column(name = "unique_users")
    private Integer uniqueUsers = 0;

    @Column(name = "peak_day")
    private LocalDate peakDay;

    @Column(name = "peak_connections")
    private Integer peakConnections = 0;

    @Column(name = "top_competition_id")
    private Long topCompetitionId;

    @Column(name = "top_competition_views")
    private Integer topCompetitionViews = 0;

    @Column(name = "total_notifications_sent")
    private Integer totalNotificationsSent = 0;

    @Column(name = "total_new_subscriptions")
    private Integer totalNewSubscriptions = 0;

    @Column(name = "avg_daily_connections")
    private Double avgDailyConnections = 0.0;

    @Column(name = "growth_rate_percent")
    private Double growthRatePercent = 0.0;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    public WeeklyStats() {}

    public Long getId() { return id; }
    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }
    public LocalDate getWeekEnd() { return weekEnd; }
    public void setWeekEnd(LocalDate weekEnd) { this.weekEnd = weekEnd; }
    public Integer getTotalConnections() { return totalConnections; }
    public void setTotalConnections(Integer totalConnections) { this.totalConnections = totalConnections; }
    public Integer getUniqueUsers() { return uniqueUsers; }
    public void setUniqueUsers(Integer uniqueUsers) { this.uniqueUsers = uniqueUsers; }
    public LocalDate getPeakDay() { return peakDay; }
    public void setPeakDay(LocalDate peakDay) { this.peakDay = peakDay; }
    public Integer getPeakConnections() { return peakConnections; }
    public void setPeakConnections(Integer peakConnections) { this.peakConnections = peakConnections; }
    public Long getTopCompetitionId() { return topCompetitionId; }
    public void setTopCompetitionId(Long topCompetitionId) { this.topCompetitionId = topCompetitionId; }
    public Integer getTopCompetitionViews() { return topCompetitionViews; }
    public void setTopCompetitionViews(Integer topCompetitionViews) { this.topCompetitionViews = topCompetitionViews; }
    public Integer getTotalNotificationsSent() { return totalNotificationsSent; }
    public void setTotalNotificationsSent(Integer totalNotificationsSent) { this.totalNotificationsSent = totalNotificationsSent; }
    public Integer getTotalNewSubscriptions() { return totalNewSubscriptions; }
    public void setTotalNewSubscriptions(Integer totalNewSubscriptions) { this.totalNewSubscriptions = totalNewSubscriptions; }
    public Double getAvgDailyConnections() { return avgDailyConnections; }
    public void setAvgDailyConnections(Double avgDailyConnections) { this.avgDailyConnections = avgDailyConnections; }
    public Double getGrowthRatePercent() { return growthRatePercent; }
    public void setGrowthRatePercent(Double growthRatePercent) { this.growthRatePercent = growthRatePercent; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
