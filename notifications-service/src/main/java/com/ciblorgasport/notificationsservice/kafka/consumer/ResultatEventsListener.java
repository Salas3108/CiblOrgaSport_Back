package com.ciblorgasport.notificationsservice.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.ciblorgasport.notificationsservice.kafka.event.ResultatFinalizedEventV1;
import com.ciblorgasport.notificationsservice.kafka.topic.KafkaTopics;
import com.ciblorgasport.notificationsservice.service.NotificationGeneratorService;

@Component
public class ResultatEventsListener {

    private static final Logger log = LoggerFactory.getLogger(ResultatEventsListener.class);

    private final NotificationGeneratorService notificationGeneratorService;

    public ResultatEventsListener(NotificationGeneratorService notificationGeneratorService) {
        this.notificationGeneratorService = notificationGeneratorService;
    }

    @KafkaListener(
            topics = KafkaTopics.RESULTAT_FINAL_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "resultatFinalKafkaListenerContainerFactory"
    )
    public void onResultatFinalized(
            ResultatFinalizedEventV1 event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        log.debug("Received resultat finalized event key={} eventId={} epreuveId={} competitionId={}",
                key,
                event != null ? event.getEventId() : null,
                event != null ? event.getEpreuveId() : null,
                event != null ? event.getCompetitionId() : null);

        notificationGeneratorService.handleResultatFinalized(event);
    }
}
