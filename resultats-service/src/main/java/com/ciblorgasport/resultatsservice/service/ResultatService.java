package com.ciblorgasport.resultatsservice.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ciblorgasport.resultatsservice.dto.request.ResultatRequest;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.model.ResultatStatut;
import com.ciblorgasport.resultatsservice.repository.ResultatRepository;

@Service
public class ResultatService {

    private final ResultatRepository resultatRepository;

    public ResultatService(ResultatRepository resultatRepository) {
        this.resultatRepository = resultatRepository;
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
