package com.ciblorgasport.resultatsservice.kafka.publisher;

import com.ciblorgasport.resultatsservice.kafka.event.ResultatFinalizedEventV1;

public interface ResultatEventPublisher {

    void publishResultatFinalized(ResultatFinalizedEventV1 event, String key);
}
