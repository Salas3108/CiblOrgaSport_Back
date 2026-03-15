package com.ciblorgasport.notificationsservice.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.ciblorgasport.notificationsservice.kafka.event.EpreuveRappelEventV1;
import com.ciblorgasport.notificationsservice.kafka.topic.KafkaTopics;
import com.ciblorgasport.notificationsservice.service.NotificationGeneratorService;

@Component
public class EpreuveEventsListener {

    private static final Logger log = LoggerFactory.getLogger(EpreuveEventsListener.class);

    private final NotificationGeneratorService notificationGeneratorService;

    public EpreuveEventsListener(NotificationGeneratorService notificationGeneratorService) {
        this.notificationGeneratorService = notificationGeneratorService;
    }

    @KafkaListener(
            topics = KafkaTopics.EPREUVE_RAPPEL_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "epreuveRappelKafkaListenerContainerFactory"
    )
    public void onEpreuveRappel(
            EpreuveRappelEventV1 event,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        log.debug("Received epreuve rappel event key={} eventId={} epreuveId={} competitionId={}",
                key,
                event != null ? event.getEventId() : null,
                event != null ? event.getEpreuveId() : null,
                event != null ? event.getCompetitionId() : null);

        notificationGeneratorService.handleEpreuveRappel(event);
    }
}
