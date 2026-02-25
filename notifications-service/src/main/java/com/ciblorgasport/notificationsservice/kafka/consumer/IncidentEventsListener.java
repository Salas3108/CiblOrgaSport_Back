package com.ciblorgasport.notificationsservice.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.ciblorgasport.notificationsservice.kafka.event.IncidentCreatedEventV1;
import com.ciblorgasport.notificationsservice.kafka.topic.KafkaTopics;
import com.ciblorgasport.notificationsservice.service.NotificationGeneratorService;

@Component
public class IncidentEventsListener {

    private static final Logger log = LoggerFactory.getLogger(IncidentEventsListener.class);

    private final NotificationGeneratorService notificationGeneratorService;

    public IncidentEventsListener(NotificationGeneratorService notificationGeneratorService) {
        this.notificationGeneratorService = notificationGeneratorService;
    }

    @KafkaListener(
            topics = KafkaTopics.INCIDENT_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onIncidentCreated(
            IncidentCreatedEventV1 event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        log.debug("Received incident event key={} eventId={} incidentId={} competitionId={}",
                key,
                event != null ? event.getEventId() : null,
                event != null ? event.getIncidentId() : null,
                event != null ? event.getCompetitionId() : null);

        notificationGeneratorService.handleIncidentCreated(event);
    }
}
