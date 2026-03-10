package com.ciblorgasport.analyticsservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_log", indexes = {
        @Index(name = "idx_event_log_timestamp", columnList = "timestamp"),
        @Index(name = "idx_event_log_user_id", columnList = "user_id"),
        @Index(name = "idx_event_log_event_type", columnList = "event_type")
})
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_role", length = 50)
    private String userRole;

    @Column(name = "event_type", length = 50)
    private String eventType;

    @Column(name = "endpoint", length = 255)
    private String endpoint;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    public EventLog() {}

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
