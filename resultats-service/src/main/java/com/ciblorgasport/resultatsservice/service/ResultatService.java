package com.ciblorgasport.resultatsservice.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciblorgasport.resultatsservice.client.dto.EpreuveContextDto;
import com.ciblorgasport.resultatsservice.dto.request.BulkResultatRequest;
import com.ciblorgasport.resultatsservice.dto.request.PerformanceEntryDto;
import com.ciblorgasport.resultatsservice.dto.request.ResultatRequest;
import com.ciblorgasport.resultatsservice.model.Discipline;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.model.ResultatStatut;
import com.ciblorgasport.resultatsservice.model.TypePerformance;
import com.ciblorgasport.resultatsservice.repository.ResultatRepository;
import com.ciblorgasport.resultatsservice.service.calcul.ClassementService;

@Service
public class ResultatService {

    private final ResultatRepository resultatRepository;
    private final ClassementService classementService;

    public ResultatService(ResultatRepository resultatRepository, ClassementService classementService) {
        this.resultatRepository = resultatRepository;
        this.classementService = classementService;
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
        return resultatRepository.save(resultat);
    }

    public List<Resultat> getClassementEpreuve(Long epreuveId, boolean publishedOnly) {
        List<Resultat> resultats = publishedOnly
                ? resultatRepository.findByEpreuveIdAndPublishedTrue(epreuveId)
                : resultatRepository.findByEpreuveId(epreuveId);
        resultats.sort(byClassement());
        return resultats;
    }

    public List<Resultat> getResultatsAthlete(Long athleteId, boolean publishedOnly) {
        List<Resultat> resultats = publishedOnly
                ? resultatRepository.findByAthleteIdAndPublishedTrue(athleteId)
                : resultatRepository.findByAthleteId(athleteId);
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
        List<Resultat> resultats = publishedOnly
                ? resultatRepository.findByEquipeIdAndPublishedTrue(equipeId)
                : resultatRepository.findByEquipeId(equipeId);
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
}
