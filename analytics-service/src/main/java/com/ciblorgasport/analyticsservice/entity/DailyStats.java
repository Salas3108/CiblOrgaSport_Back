package com.ciblorgasport.analyticsservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_stats")
public class DailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_date", unique = true, nullable = false)
    private LocalDate statDate;

    @Column(name = "total_connections")
    private Integer totalConnections = 0;

    @Column(name = "unique_users")
    private Integer uniqueUsers = 0;

    @Column(name = "connections_athletes")
    private Integer connectionsAthletes = 0;

    @Column(name = "connections_spectateurs")
    private Integer connectionsSpectateurs = 0;

    @Column(name = "connections_commissaires")
    private Integer connectionsCommissaires = 0;

    @Column(name = "connections_volontaires")
    private Integer connectionsVolontaires = 0;

    @Column(name = "connections_admins")
    private Integer connectionsAdmins = 0;

    @Column(name = "total_page_views")
    private Integer totalPageViews = 0;

    @Column(name = "total_notifications_sent")
    private Integer totalNotificationsSent = 0;

    @Column(name = "notifications_resultats")
    private Integer notificationsResultats = 0;

    @Column(name = "notifications_securite")
    private Integer notificationsSecurite = 0;

    @Column(name = "notifications_events")
    private Integer notificationsEvents = 0;

    @Column(name = "total_subscriptions")
    private Integer totalSubscriptions = 0;

    @Column(name = "total_competition_views")
    private Integer totalCompetitionViews = 0;

    @Column(name = "total_epreuve_views")
    private Integer totalEpreuveViews = 0;

    @Column(name = "total_result_views")
    private Integer totalResultViews = 0;

    @Column(name = "total_event_views")
    private Integer totalEventViews = 0;

    @Column(name = "total_athlete_profile_views")
    private Integer totalAthleteProfileViews = 0;

    @Column(name = "avg_session_duration_ms")
    private Long avgSessionDurationMs = 0L;

    @Column(name = "avg_response_time_ms")
    private Long avgResponseTimeMs = 0L;

    @Column(name = "total_incidents")
    private Integer totalIncidents = 0;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    public DailyStats() {}

    public Long getId() { return id; }
    public LocalDate getStatDate() { return statDate; }
    public void setStatDate(LocalDate statDate) { this.statDate = statDate; }
    public Integer getTotalConnections() { return totalConnections; }
    public void setTotalConnections(Integer totalConnections) { this.totalConnections = totalConnections; }
    public Integer getUniqueUsers() { return uniqueUsers; }
    public void setUniqueUsers(Integer uniqueUsers) { this.uniqueUsers = uniqueUsers; }
    public Integer getConnectionsAthletes() { return connectionsAthletes; }
    public void setConnectionsAthletes(Integer connectionsAthletes) { this.connectionsAthletes = connectionsAthletes; }
    public Integer getConnectionsSpectateurs() { return connectionsSpectateurs; }
    public void setConnectionsSpectateurs(Integer connectionsSpectateurs) { this.connectionsSpectateurs = connectionsSpectateurs; }
    public Integer getConnectionsCommissaires() { return connectionsCommissaires; }
    public void setConnectionsCommissaires(Integer connectionsCommissaires) { this.connectionsCommissaires = connectionsCommissaires; }
    public Integer getConnectionsVolontaires() { return connectionsVolontaires; }
    public void setConnectionsVolontaires(Integer connectionsVolontaires) { this.connectionsVolontaires = connectionsVolontaires; }
    public Integer getConnectionsAdmins() { return connectionsAdmins; }
    public void setConnectionsAdmins(Integer connectionsAdmins) { this.connectionsAdmins = connectionsAdmins; }
    public Integer getTotalPageViews() { return totalPageViews; }
    public void setTotalPageViews(Integer totalPageViews) { this.totalPageViews = totalPageViews; }
    public Integer getTotalNotificationsSent() { return totalNotificationsSent; }
    public void setTotalNotificationsSent(Integer totalNotificationsSent) { this.totalNotificationsSent = totalNotificationsSent; }
    public Integer getNotificationsResultats() { return notificationsResultats; }
    public void setNotificationsResultats(Integer notificationsResultats) { this.notificationsResultats = notificationsResultats; }
    public Integer getNotificationsSecurite() { return notificationsSecurite; }
    public void setNotificationsSecurite(Integer notificationsSecurite) { this.notificationsSecurite = notificationsSecurite; }
    public Integer getNotificationsEvents() { return notificationsEvents; }
    public void setNotificationsEvents(Integer notificationsEvents) { this.notificationsEvents = notificationsEvents; }
    public Integer getTotalSubscriptions() { return totalSubscriptions; }
    public void setTotalSubscriptions(Integer totalSubscriptions) { this.totalSubscriptions = totalSubscriptions; }
    public Integer getTotalCompetitionViews() { return totalCompetitionViews; }
    public void setTotalCompetitionViews(Integer totalCompetitionViews) { this.totalCompetitionViews = totalCompetitionViews; }
    public Integer getTotalEpreuveViews() { return totalEpreuveViews; }
    public void setTotalEpreuveViews(Integer totalEpreuveViews) { this.totalEpreuveViews = totalEpreuveViews; }
    public Integer getTotalResultViews() { return totalResultViews; }
    public void setTotalResultViews(Integer totalResultViews) { this.totalResultViews = totalResultViews; }
    public Integer getTotalEventViews() { return totalEventViews; }
    public void setTotalEventViews(Integer totalEventViews) { this.totalEventViews = totalEventViews; }
    public Integer getTotalAthleteProfileViews() { return totalAthleteProfileViews; }
    public void setTotalAthleteProfileViews(Integer totalAthleteProfileViews) { this.totalAthleteProfileViews = totalAthleteProfileViews; }
    public Long getAvgSessionDurationMs() { return avgSessionDurationMs; }
    public void setAvgSessionDurationMs(Long avgSessionDurationMs) { this.avgSessionDurationMs = avgSessionDurationMs; }
    public Long getAvgResponseTimeMs() { return avgResponseTimeMs; }
    public void setAvgResponseTimeMs(Long avgResponseTimeMs) { this.avgResponseTimeMs = avgResponseTimeMs; }
    public Integer getTotalIncidents() { return totalIncidents; }
    public void setTotalIncidents(Integer totalIncidents) { this.totalIncidents = totalIncidents; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
