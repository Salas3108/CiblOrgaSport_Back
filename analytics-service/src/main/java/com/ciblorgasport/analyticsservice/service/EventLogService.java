package com.ciblorgasport.analyticsservice.service;

import com.ciblorgasport.analyticsservice.dto.EventLogRequest;
import com.ciblorgasport.analyticsservice.entity.EventLog;
import com.ciblorgasport.analyticsservice.repository.EventLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventLogService {

    private static final Logger log = LoggerFactory.getLogger(EventLogService.class);

    private final EventLogRepository eventLogRepository;

    public EventLogService(EventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    /**
     * Sauvegarde un événement de façon asynchrone.
     * En cas d'échec, on log un WARN sans faire échouer la requête principale.
     */
    @Async("analyticsExecutor")
    public void saveAsync(EventLogRequest request) {
        try {
            EventLog event = new EventLog();
            event.setUserId(request.getUserId());
            event.setUserRole(request.getUserRole() != null ? request.getUserRole() : "ANONYMOUS");
            event.setEventType(request.getEventType() != null ? request.getEventType() : "PAGE_VIEW");
            event.setEndpoint(truncate(request.getEndpoint(), 255));
            event.setHttpMethod(request.getHttpMethod());
            event.setStatusCode(request.getStatusCode());
            event.setDurationMs(request.getDurationMs());
            event.setIpAddress(truncate(request.getIpAddress(), 50));
            event.setTimestamp(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now());
            event.setMetadata(request.getMetadata());
            eventLogRepository.save(event);
        } catch (Exception e) {
            log.warn("Échec de l'enregistrement de l'événement analytics (fail silently) : {}", e.getMessage());
        }
    }

    /**
     * Sauvegarde synchrone — utilisée par l'AOP interne.
     */
    public void save(EventLogRequest request) {
        saveAsync(request);
    }

    public List<EventLog> getLiveEvents() {
        return eventLogRepository.findTop50ByOrderByTimestampDesc();
    }

    public Map<String, Long> getEventCountsForToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<Object[]> rows = eventLogRepository.countByEventTypeGroupedForPeriod(startOfDay, endOfDay);
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    public List<Object[]> getTopCompetitions(LocalDateTime start, LocalDateTime end) {
        return eventLogRepository.findTopCompetitions(start, end);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
