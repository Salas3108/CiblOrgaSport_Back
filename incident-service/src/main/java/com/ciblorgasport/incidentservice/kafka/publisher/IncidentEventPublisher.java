package com.ciblorgasport.incidentservice.kafka.publisher;

import com.ciblorgasport.incidentservice.kafka.event.IncidentCreatedEventV1;

public interface IncidentEventPublisher {

    void publishIncidentCreated(IncidentCreatedEventV1 event, String key);
}
