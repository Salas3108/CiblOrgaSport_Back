package com.ciblorgasport.participantsservice.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ciblorgasport.participantsservice.dto.request.AssignAthletesRequest;
import com.ciblorgasport.participantsservice.model.EpreuveAthleteAssignment;
import com.ciblorgasport.participantsservice.repository.JpaAthleteRepository;
import com.ciblorgasport.participantsservice.repository.JpaEpreuveAthleteAssignmentRepository;

@Service
public class EpreuveAssignmentService {

    private final JpaEpreuveAthleteAssignmentRepository assignmentRepository;
    private final JpaAthleteRepository athleteRepository;
    private final ParticipantsStore store;

    public EpreuveAssignmentService(JpaEpreuveAthleteAssignmentRepository assignmentRepository,
                                    JpaAthleteRepository athleteRepository,
                                    ParticipantsStore store) {
        this.assignmentRepository = assignmentRepository;
        this.athleteRepository = athleteRepository;
        this.store = store;
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
