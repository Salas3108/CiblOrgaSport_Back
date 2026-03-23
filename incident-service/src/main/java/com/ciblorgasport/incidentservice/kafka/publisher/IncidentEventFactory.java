package com.ciblorgasport.incidentservice.kafka.publisher;

import java.time.Instant;
import java.util.UUID;

import com.ciblorgasport.incidentservice.kafka.event.IncidentCreatedEventV1;
import com.ciblorgasport.incidentservice.model.Incident;

public final class IncidentEventFactory {

    private IncidentEventFactory() {
    }

    public static IncidentCreatedEventV1 incidentCreated(Incident incident) {
        IncidentCreatedEventV1 event = new IncidentCreatedEventV1();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(IncidentCreatedEventV1.EVENT_TYPE_VALUE);
        event.setVersion(IncidentCreatedEventV1.EVENT_VERSION);
        event.setOccurredAt(Instant.now());

        event.setIncidentId(incident.getId());
        event.setIncidentType(incident.getType() != null ? incident.getType().name() : null);
        event.setImpactLevel(incident.getImpactLevel() != null ? incident.getImpactLevel().name() : null);
        event.setStatus(incident.getStatus() != null ? incident.getStatus().name() : null);

        event.setDescription(incident.getDescription());
        event.setLieuId(incident.getLieuId());
        event.setReportedBy(incident.getReportedBy());
        event.setCompetitionId(incident.getCompetitionId());

        return event;
    }

    public static String partitionKey(Incident incident) {
        if (incident.getCompetitionId() != null) {
            return "competition-" + incident.getCompetitionId();
        }
        if (incident.getLieuId() != null) {
            return "lieu-" + incident.getLieuId();
        }
        return "incident-" + incident.getId();
    }
}
