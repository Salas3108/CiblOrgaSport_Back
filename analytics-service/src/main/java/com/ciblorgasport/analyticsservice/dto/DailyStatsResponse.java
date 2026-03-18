package com.ciblorgasport.analyticsservice.dto;

import com.ciblorgasport.analyticsservice.entity.DailyStats;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyStatsResponse {

    private Long id;
    private LocalDate statDate;
    private Integer totalConnections;
    private Integer uniqueUsers;
    private Integer connectionsAthletes;
    private Integer connectionsSpectateurs;
    private Integer connectionsCommissaires;
    private Integer connectionsVolontaires;
    private Integer connectionsAdmins;
    private Integer totalPageViews;
    private Integer totalNotificationsSent;
    private Integer notificationsResultats;
    private Integer notificationsSecurite;
    private Integer notificationsEvents;
    private Integer totalSubscriptions;
    private Integer totalCompetitionViews;
    private Integer totalResultViews;
    private Long avgSessionDurationMs;
    private Long avgResponseTimeMs;
    private Integer totalIncidents;
    private LocalDateTime calculatedAt;

    public static DailyStatsResponse from(DailyStats s) {
        DailyStatsResponse r = new DailyStatsResponse();
        r.id = s.getId();
        r.statDate = s.getStatDate();
        r.totalConnections = s.getTotalConnections();
        r.uniqueUsers = s.getUniqueUsers();
        r.connectionsAthletes = s.getConnectionsAthletes();
        r.connectionsSpectateurs = s.getConnectionsSpectateurs();
        r.connectionsCommissaires = s.getConnectionsCommissaires();
        r.connectionsVolontaires = s.getConnectionsVolontaires();
        r.connectionsAdmins = s.getConnectionsAdmins();
        r.totalPageViews = s.getTotalPageViews();
        r.totalNotificationsSent = s.getTotalNotificationsSent();
        r.notificationsResultats = s.getNotificationsResultats();
        r.notificationsSecurite = s.getNotificationsSecurite();
        r.notificationsEvents = s.getNotificationsEvents();
        r.totalSubscriptions = s.getTotalSubscriptions();
        r.totalCompetitionViews = s.getTotalCompetitionViews();
        r.totalResultViews = s.getTotalResultViews();
        r.avgSessionDurationMs = s.getAvgSessionDurationMs();
        r.avgResponseTimeMs = s.getAvgResponseTimeMs();
        r.totalIncidents = s.getTotalIncidents();
        r.calculatedAt = s.getCalculatedAt();
        return r;
    }

    public Long getId() { return id; }
    public LocalDate getStatDate() { return statDate; }
    public Integer getTotalConnections() { return totalConnections; }
    public Integer getUniqueUsers() { return uniqueUsers; }
    public Integer getConnectionsAthletes() { return connectionsAthletes; }
    public Integer getConnectionsSpectateurs() { return connectionsSpectateurs; }
    public Integer getConnectionsCommissaires() { return connectionsCommissaires; }
    public Integer getConnectionsVolontaires() { return connectionsVolontaires; }
    public Integer getConnectionsAdmins() { return connectionsAdmins; }
    public Integer getTotalPageViews() { return totalPageViews; }
    public Integer getTotalNotificationsSent() { return totalNotificationsSent; }
    public Integer getNotificationsResultats() { return notificationsResultats; }
    public Integer getNotificationsSecurite() { return notificationsSecurite; }
    public Integer getNotificationsEvents() { return notificationsEvents; }
    public Integer getTotalSubscriptions() { return totalSubscriptions; }
    public Integer getTotalCompetitionViews() { return totalCompetitionViews; }
    public Integer getTotalResultViews() { return totalResultViews; }
    public Long getAvgSessionDurationMs() { return avgSessionDurationMs; }
    public Long getAvgResponseTimeMs() { return avgResponseTimeMs; }
    public Integer getTotalIncidents() { return totalIncidents; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
}
