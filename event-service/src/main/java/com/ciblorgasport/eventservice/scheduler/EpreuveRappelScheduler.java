package com.ciblorgasport.eventservice.scheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ciblorgasport.eventservice.kafka.event.EpreuveRappelEventV1;
import com.ciblorgasport.eventservice.kafka.publisher.EpreuveEventPublisher;
import com.ciblorgasport.eventservice.model.Epreuve;
import com.ciblorgasport.eventservice.repository.EpreuveRepository;

@Component
public class EpreuveRappelScheduler {

    private static final Logger log = LoggerFactory.getLogger(EpreuveRappelScheduler.class);

    private final EpreuveRepository epreuveRepository;
    private final EpreuveEventPublisher epreuveEventPublisher;

    @Value("${epreuve.rappel.windowMinHours:1}")
    private int windowMinHours;

    @Value("${epreuve.rappel.windowMaxHours:25}")
    private int windowMaxHours;

    public EpreuveRappelScheduler(EpreuveRepository epreuveRepository,
                                  EpreuveEventPublisher epreuveEventPublisher) {
        this.epreuveRepository = epreuveRepository;
        this.epreuveEventPublisher = epreuveEventPublisher;
    }

    @Scheduled(cron = "${epreuve.rappel.cron:0 0 * * * *}")
    public void envoyerRappelsEpreuves() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusHours(windowMinHours);
        LocalDateTime to = now.plusHours(windowMaxHours);

        List<Epreuve> epreuves = epreuveRepository.findByDateHeureBetween(from, to);

        if (epreuves.isEmpty()) {
            log.debug("Rappel épreuve: aucune épreuve dans la fenêtre [{} - {}]", from, to);
            return;
        }

        log.info("Rappel épreuve: {} épreuve(s) à notifier dans la fenêtre [{} - {}]",
                epreuves.size(), from, to);

        for (Epreuve epreuve : epreuves) {
            if (epreuve.getCompetition() == null) {
                log.warn("Epreuve id={} n'a pas de competition associée, ignorée", epreuve.getId());
                continue;
            }

            EpreuveRappelEventV1 event = new EpreuveRappelEventV1();
            event.setEventId(UUID.randomUUID().toString());
            event.setEventType(EpreuveRappelEventV1.EVENT_TYPE_VALUE);
            event.setVersion(EpreuveRappelEventV1.EVENT_VERSION);
            event.setOccurredAt(Instant.now());
            event.setEpreuveId(epreuve.getId());
            event.setNom(epreuve.getNom());
            event.setDateHeure(epreuve.getDateHeure());
            event.setCompetitionId(epreuve.getCompetition().getId());

            String key = "competition-" + epreuve.getCompetition().getId();
            epreuveEventPublisher.publishEpreuveRappel(event, key);

            log.debug("Rappel publié pour epreuveId={} competitionId={}", epreuve.getId(), epreuve.getCompetition().getId());
        }
    }
}
