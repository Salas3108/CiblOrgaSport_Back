package com.ciblorgasport.resultatsservice.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciblorgasport.resultatsservice.client.dto.EpreuveContextDto;
import com.ciblorgasport.resultatsservice.dto.request.BulkResultatRequest;
import com.ciblorgasport.resultatsservice.dto.request.PerformanceEntryDto;
import com.ciblorgasport.resultatsservice.client.EventServiceClient;
import com.ciblorgasport.resultatsservice.client.ParticipantsServiceClient;
import com.ciblorgasport.resultatsservice.dto.request.ResultatRequest;
import com.ciblorgasport.resultatsservice.model.Discipline;
import com.ciblorgasport.resultatsservice.kafka.event.ResultatFinalizedEventV1;
import com.ciblorgasport.resultatsservice.kafka.publisher.ResultatEventPublisher;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.model.ResultatStatut;
import com.ciblorgasport.resultatsservice.model.TypePerformance;
import com.ciblorgasport.resultatsservice.repository.ResultatRepository;
import com.ciblorgasport.resultatsservice.service.calcul.ClassementService;

@Service
public class ResultatService {

    private final ResultatRepository resultatRepository;
    private final ClassementService classementService;
    private final ResultatEventPublisher resultatEventPublisher;
    private final EventServiceClient eventServiceClient;
    private final ParticipantsServiceClient participantsServiceClient;

    public ResultatService(ResultatRepository resultatRepository, ClassementService classementService,
                          ResultatEventPublisher resultatEventPublisher,
                          EventServiceClient eventServiceClient,
                          ParticipantsServiceClient participantsServiceClient) {
        this.resultatRepository = resultatRepository;
        this.classementService = classementService;
        this.resultatEventPublisher = resultatEventPublisher;
        this.eventServiceClient = eventServiceClient;
        this.participantsServiceClient = participantsServiceClient;
    }

    public Resultat createOrUpdate(ResultatRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request est obligatoire");
        }
        if (request.getEpreuveId() == null) {
            throw new IllegalArgumentException("epreuveId est obligatoire");
        }
        validateParticipant(request.getAthleteId(), request.getEquipeId());

        Resultat existing = findExisting(request.getEpreuveId(), request.getAthleteId(), request.getEquipeId());
        if (existing == null) {
            Resultat created = new Resultat();
            created.setEpreuveId(request.getEpreuveId());
            created.setAthleteId(request.getAthleteId());
            created.setEquipeId(request.getEquipeId());
            applyRequest(created, request, true);
            created.setStatut(ResultatStatut.EN_ATTENTE);
            created.setPublished(false);
            return resultatRepository.save(created);
        }

        applyRequest(existing, request, false);
        return resultatRepository.save(existing);
    }

    public Resultat validateResultat(Long id) {
        Resultat resultat = findByIdOrThrow(id);
        resultat.setStatut(ResultatStatut.VALIDE);
        return resultatRepository.save(resultat);
    }

    public Resultat publishResultat(Long id) {
        Resultat resultat = findByIdOrThrow(id);
        if (resultat.getStatut() != ResultatStatut.VALIDE) {
            throw new IllegalArgumentException("resultat doit etre VALIDE avant publication");
        }
        resultat.setPublished(true);
        Resultat saved = resultatRepository.save(resultat);
        maybePublishFinalizedEvent(saved.getEpreuveId());
        return saved;
    }

    public List<Resultat> getClassementEpreuve(Long epreuveId, boolean publishedOnly) {
        List<Resultat> resultats = new ArrayList<>(publishedOnly
                ? resultatRepository.findByEpreuveIdAndPublishedTrue(epreuveId)
            : resultatRepository.findByEpreuveId(epreuveId));
        resultats.sort(byClassement());
        return resultats;
    }

    public List<Resultat> getResultatsAthlete(Long athleteId, boolean publishedOnly) {
        List<Resultat> resultats = new ArrayList<>(publishedOnly
                ? resultatRepository.findByAthleteIdAndPublishedTrue(athleteId)
            : resultatRepository.findByAthleteId(athleteId));
        resultats.sort(byEpreuveThenClassement());
        return resultats;
    }

    /**
     * Saisie en masse des performances d'une épreuve.
     * Persiste chaque entrée, puis déclenche le calcul automatique du classement.
     */
    @Transactional
    public List<Resultat> saisirBulk(Long epreuveId, BulkResultatRequest request, EpreuveContextDto ctx) {
        TypePerformance typePerformance = resolveTypePerformance(ctx.getDiscipline());

        for (PerformanceEntryDto entry : request.getPerformances()) {
            ResultatRequest req = new ResultatRequest();
            req.setEpreuveId(epreuveId);
            req.setAthleteId(entry.getAthleteId());
            req.setEquipeId(entry.getEquipeId());
            req.setValeurPrincipale(entry.getValeurPrincipale());
            req.setDetailsPerformance(entry.getDetailsPerformance());
            req.setTypePerformance(typePerformance);
            createOrUpdate(req);
        }

        classementService.calculerClassementAvecContexte(epreuveId, ctx);
        return resultatRepository.findByEpreuveId(epreuveId);
    }

    /**
     * Valide tous les résultats EN_ATTENTE d'une épreuve.
     */
    @Transactional
    public List<Resultat> validerTout(Long epreuveId) {
        List<Resultat> enAttente = resultatRepository.findByEpreuveId(epreuveId).stream()
                .filter(r -> r.getStatut() == ResultatStatut.EN_ATTENTE)
                .toList();
        enAttente.forEach(r -> r.setStatut(ResultatStatut.VALIDE));
        return resultatRepository.saveAll(enAttente);
    }

    private TypePerformance resolveTypePerformance(String discipline) {
        if (discipline == null) return TypePerformance.TEMPS;
        try {
            return switch (Discipline.valueOf(discipline)) {
                case NATATION, EAU_LIBRE -> TypePerformance.TEMPS;
                case WATER_POLO -> TypePerformance.SCORE;
                case PLONGEON, NATATION_ARTISTIQUE -> TypePerformance.POINTS;
            };
        } catch (IllegalArgumentException e) {
            return TypePerformance.TEMPS;
        }
    }

    public List<Resultat> getResultatsEquipe(Long equipeId, boolean publishedOnly) {
        List<Resultat> resultats = new ArrayList<>(publishedOnly
                ? resultatRepository.findByEquipeIdAndPublishedTrue(equipeId)
            : resultatRepository.findByEquipeId(equipeId));
        resultats.sort(byEpreuveThenClassement());
        return resultats;
    }

    private Comparator<Resultat> byClassement() {
        return Comparator.comparing(Resultat::getClassement, Comparator.nullsLast(Integer::compareTo));
    }

    private Comparator<Resultat> byEpreuveThenClassement() {
        return Comparator
                .comparing(Resultat::getEpreuveId, Comparator.nullsLast(Long::compareTo))
                .thenComparing(Resultat::getClassement, Comparator.nullsLast(Integer::compareTo));
    }

    private Resultat findExisting(Long epreuveId, Long athleteId, Long equipeId) {
        if (athleteId != null) {
            return resultatRepository.findByEpreuveIdAndAthleteId(epreuveId, athleteId).orElse(null);
        }
        if (equipeId != null) {
            return resultatRepository.findByEpreuveIdAndEquipeId(epreuveId, equipeId).orElse(null);
        }
        return null;
    }

    private void applyRequest(Resultat resultat, ResultatRequest request, boolean isCreate) {
        if (request.getClassement() != null || isCreate) {
            resultat.setClassement(request.getClassement());
        }
        if (request.getMedaille() != null || isCreate) {
            resultat.setMedaille(request.getMedaille());
        }
        if (request.getQualification() != null || isCreate) {
            resultat.setQualification(Boolean.TRUE.equals(request.getQualification()));
        }
        if (request.getValeurPrincipale() != null || isCreate) {
            resultat.setValeurPrincipale(request.getValeurPrincipale());
        }
        if (request.getUnite() != null || isCreate) {
            resultat.setUnite(request.getUnite());
        }
        if (request.getDetailsPerformance() != null || isCreate) {
            resultat.setDetailsPerformance(request.getDetailsPerformance());
        }
        if (request.getTypePerformance() != null || isCreate) {
            resultat.setTypePerformance(request.getTypePerformance());
        }
    }

    private void validateParticipant(Long athleteId, Long equipeId) {
        if (athleteId == null && equipeId == null) {
            throw new IllegalArgumentException("athleteId ou equipeId est obligatoire");
        }
        if (athleteId != null && equipeId != null) {
            throw new IllegalArgumentException("athleteId et equipeId sont mutuellement exclusifs");
        }
    }

    public Resultat findByIdOrThrow(Long id) {
        return resultatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resultat introuvable: " + id));
    }

    private void maybePublishFinalizedEvent(Long epreuveId) {
        if (epreuveId == null) {
            return;
        }

        List<Resultat> publishedResultats = getClassementEpreuve(epreuveId, true);
        DisplayPayload payload = buildDisplayPayload(publishedResultats);
        if (payload == null || payload.lines().isEmpty()) {
            return;
        }

        Optional<EventServiceClient.EpreuveSummary> epreuveSummaryOpt = eventServiceClient.getEpreuveSummary(epreuveId);
        if (epreuveSummaryOpt.isEmpty() || epreuveSummaryOpt.get().getCompetitionId() == null) {
            return;
        }

        EventServiceClient.EpreuveSummary epreuveSummary = epreuveSummaryOpt.get();

        ResultatFinalizedEventV1 event = new ResultatFinalizedEventV1();
        event.setEventId("resultat-final-" + epreuveId);
        event.setEventType(ResultatFinalizedEventV1.EVENT_TYPE_VALUE);
        event.setVersion(ResultatFinalizedEventV1.EVENT_VERSION);
        event.setOccurredAt(Instant.now());
        event.setCompetitionId(epreuveSummary.getCompetitionId());
        event.setEpreuveId(epreuveId);
        event.setNomEpreuve(epreuveSummary.getNom());
        event.setDisplayMode(payload.displayMode());
        event.setLines(payload.lines());

        resultatEventPublisher.publishResultatFinalized(event, "epreuve-" + epreuveId);
    }

    private DisplayPayload buildDisplayPayload(List<Resultat> publishedResultats) {
        if (publishedResultats == null || publishedResultats.isEmpty()) {
            return null;
        }

        Resultat first = findByClassement(publishedResultats, 1);
        Resultat second = findByClassement(publishedResultats, 2);
        Resultat third = findByClassement(publishedResultats, 3);

        if (first != null && second != null && third != null) {
            return new DisplayPayload(
                    "PODIUM_3",
                    List.of(toLine(first), toLine(second), toLine(third))
            );
        }

        if (publishedResultats.size() == 2) {
            return new DisplayPayload(
                    "DUEL_2",
                    List.of(toLine(publishedResultats.get(0)), toLine(publishedResultats.get(1)))
            );
        }

        if (first != null) {
            return new DisplayPayload("WINNER_ONLY", List.of(toLine(first)));
        }

        return null;
    }

    private Resultat findByClassement(List<Resultat> resultats, int classement) {
        return resultats.stream()
                .filter(r -> r.getClassement() != null && r.getClassement() == classement)
                .findFirst()
                .orElse(null);
    }

    private ResultatFinalizedEventV1.ResultLine toLine(Resultat resultat) {
        ResultatFinalizedEventV1.ResultLine line = new ResultatFinalizedEventV1.ResultLine();
        line.setClassement(resultat.getClassement());
        line.setParticipant(resolveParticipantLabel(resultat));
        line.setPerformance(resolvePerformance(resultat));
        return line;
    }

    private String resolveParticipantLabel(Resultat resultat) {
        if (resultat.getAthleteId() != null) {
            return participantsServiceClient.getAthleteDisplayName(resultat.getAthleteId())
                    .orElse("Athlete #" + resultat.getAthleteId());
        }
        if (resultat.getEquipeId() != null) {
            return participantsServiceClient.getEquipeDisplayName(resultat.getEquipeId())
                    .orElse("Equipe #" + resultat.getEquipeId());
        }
        return "Participant inconnu";
    }

    private String resolvePerformance(Resultat resultat) {
        String value = resultat.getValeurPrincipale();
        String unit = resultat.getUnite();

        if (value != null && !value.isBlank()) {
            if (unit != null && !unit.isBlank()) {
                return value + " " + unit;
            }
            return value;
        }

        return "N/A";
    }

    private record DisplayPayload(String displayMode, List<ResultatFinalizedEventV1.ResultLine> lines) {
    }
}
