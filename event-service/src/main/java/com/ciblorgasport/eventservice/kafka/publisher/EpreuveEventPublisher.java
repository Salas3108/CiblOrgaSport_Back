package com.ciblorgasport.eventservice.kafka.publisher;

import com.ciblorgasport.eventservice.kafka.event.EpreuveRappelEventV1;

public interface EpreuveEventPublisher {

    void publishEpreuveRappel(EpreuveRappelEventV1 event, String key);
}
