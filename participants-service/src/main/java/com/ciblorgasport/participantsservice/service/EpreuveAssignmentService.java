package com.ciblorgasport.participantsservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ciblorgasport.participantsservice.dto.ForfaitResponse;
import com.ciblorgasport.participantsservice.dto.request.AssignAthletesRequest;
import com.ciblorgasport.participantsservice.dto.request.ForfaitRequest;
import com.ciblorgasport.participantsservice.model.EpreuveAthleteAssignment;
import com.ciblorgasport.participantsservice.model.StatutParticipation;
import com.ciblorgasport.participantsservice.repository.JpaAthleteRepository;
import com.ciblorgasport.participantsservice.repository.JpaEpreuveAthleteAssignmentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EpreuveAssignmentService {

    private final JpaEpreuveAthleteAssignmentRepository assignmentRepository;
    private final JpaAthleteRepository athleteRepository;
    private final ParticipantsStore store;
    private final ObjectMapper objectMapper;

    public EpreuveAssignmentService(JpaEpreuveAthleteAssignmentRepository assignmentRepository,
                                    JpaAthleteRepository athleteRepository,
                                    ParticipantsStore store,
                                    ObjectMapper objectMapper) {
        this.assignmentRepository = assignmentRepository;
        this.athleteRepository = athleteRepository;
        this.store = store;
        this.objectMapper = objectMapper;
    }

    public List<Long> assignAthletes(Long epreuveId, AssignAthletesRequest request) {
        if (epreuveId == null) {
            throw new IllegalArgumentException("epreuveId est obligatoire");
        }
        if (request == null || request.getAthleteIds() == null || request.getAthleteIds().isEmpty()) {
            throw new IllegalArgumentException("athleteIds est obligatoire");
        }
        List<Long> ids = request.getAthleteIds();
        for (Long id : ids) {
            if (id == null) {
                throw new IllegalArgumentException("athleteIds ne doit pas contenir de null");
            }
        }
        List<EpreuveAthleteAssignment> existing = assignmentRepository.findByEpreuveId(epreuveId);
        List<Long> existingIds = existing.stream()
                .map(EpreuveAthleteAssignment::getAthleteId)
                .collect(Collectors.toList());
        List<Long> alreadyAssigned = ids.stream()
                .filter(existingIds::contains)
                .collect(Collectors.toList());
        if (!alreadyAssigned.isEmpty()) {
            throw new IllegalArgumentException("athlete deja assigne a l'epreuve: " + alreadyAssigned);
        }
        List<Long> missingAthletes = ids.stream()
                .filter(id -> !athleteRepository.existsById(id))
                .collect(Collectors.toList());
        if (!missingAthletes.isEmpty()) {
            throw new IllegalArgumentException("Athlete(s) introuvable(s): " + missingAthletes);
        }
        List<EpreuveAthleteAssignment> toSave = new ArrayList<>();
        for (Long athleteId : ids) {
            toSave.add(new EpreuveAthleteAssignment(epreuveId, athleteId));
        }
        assignmentRepository.saveAll(toSave);
        store.addLog("COMMISSAIRE assign athletes epreuveId=" + epreuveId + " count=" + ids.size());
        return listAthletesForEpreuve(epreuveId);
    }

    public List<Long> listAthletesForEpreuve(Long epreuveId) {
        if (epreuveId == null) {
            throw new IllegalArgumentException("epreuveId est obligatoire");
        }
        return assignmentRepository.findByEpreuveId(epreuveId).stream()
                .map(EpreuveAthleteAssignment::getAthleteId)
                .distinct()
                .collect(Collectors.toList());
    }

    public ForfaitResponse declarerForfait(Long epreuveId, Long athleteId, ForfaitRequest request) {
        EpreuveAthleteAssignment assignment = assignmentRepository
                .findByAthleteIdAndEpreuveId(athleteId, epreuveId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Participation introuvable pour epreuveId=" + epreuveId + " et athleteId=" + athleteId));

        if (StatutParticipation.TERMINE.equals(assignment.getStatutParticipation())) {
            throw new IllegalStateException(
                    "Forfait impossible : l'athlète " + athleteId + " a déjà terminé l'épreuve " + epreuveId + " (statut TERMINE)");
        }

        assignment.setStatutParticipation(StatutParticipation.FORFAIT);
        assignment.setDateForfait(LocalDateTime.now());

        if (request != null && request.getDetailsPerformance() != null) {
            try {
                assignment.setDetailsPerformance(objectMapper.writeValueAsString(request.getDetailsPerformance()));
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("detailsPerformance invalide : " + e.getMessage());
            }
        }

        assignmentRepository.save(assignment);
        store.addLog("FORFAIT epreuveId=" + epreuveId + " athleteId=" + athleteId);

        ForfaitResponse response = new ForfaitResponse();
        response.setEpreuveId(epreuveId);
        response.setAthleteId(athleteId);
        response.setStatutParticipation(StatutParticipation.FORFAIT.name());
        response.setDateForfait(assignment.getDateForfait());
        response.setDetailsPerformance(assignment.getDetailsPerformance());
        response.setMessage("Forfait enregistré avec succès");
        return response;
    }

    public StatutParticipation getStatutParticipation(Long epreuveId, Long athleteId) {
        return assignmentRepository.findByAthleteIdAndEpreuveId(athleteId, epreuveId)
                .map(EpreuveAthleteAssignment::getStatutParticipation)
                .orElse(StatutParticipation.INSCRIT);
    }

    public Map<Long, List<Long>> listAllAssignments() {
        Map<Long, List<Long>> result = new LinkedHashMap<>();
        List<EpreuveAthleteAssignment> all = assignmentRepository.findAll();
        for (EpreuveAthleteAssignment assignment : all) {
            Long epreuveId = assignment.getEpreuveId();
            result.computeIfAbsent(epreuveId, key -> new ArrayList<>())
                  .add(assignment.getAthleteId());
        }
        return result;
    }
}
