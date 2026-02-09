package com.ciblorgasport.participantsservice.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ciblorgasport.participantsservice.dto.request.AssignAthletesRequest;
import com.ciblorgasport.participantsservice.dto.request.CreateEquipeRequest;
import com.ciblorgasport.participantsservice.dto.request.UpdateEquipeRequest;
import com.ciblorgasport.participantsservice.model.Athlete;
import com.ciblorgasport.participantsservice.model.Equipe;
import com.ciblorgasport.participantsservice.repository.JpaAthleteRepository;
import com.ciblorgasport.participantsservice.repository.JpaEquipeRepository;

@Service
public class EquipeService {

    private final JpaEquipeRepository equipeRepository;
    private final JpaAthleteRepository athleteRepository;
    private final ParticipantsStore store;

    public EquipeService(JpaEquipeRepository equipeRepository,
                         JpaAthleteRepository athleteRepository,
                         ParticipantsStore store) {
        this.equipeRepository = equipeRepository;
        this.athleteRepository = athleteRepository;
        this.store = store;
    }

    public List<Equipe> findAll() {
        return equipeRepository.findAll();
    }

    public Equipe findByIdOrThrow(Long id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipe introuvable: " + id));
    }

    public Equipe create(CreateEquipeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request est obligatoire");
        }
        if (request.getNom() == null || request.getNom().isBlank()) {
            throw new IllegalArgumentException("nom est obligatoire");
        }
        if (request.getPays() == null || request.getPays().isBlank()) {
            throw new IllegalArgumentException("pays est obligatoire");
        }
        Equipe equipe = new Equipe();
        equipe.setNom(request.getNom());
        equipe.setPays(request.getPays());
        store.addLog("COMMISSAIRE create equipe nom=" + request.getNom());
        return equipeRepository.save(equipe);
    }

    public Equipe update(Long id, UpdateEquipeRequest request) {
        Equipe equipe = findByIdOrThrow(id);
        if (request == null) {
            throw new IllegalArgumentException("request est obligatoire");
        }
        if (request.getNom() != null) {
            equipe.setNom(request.getNom());
        }
        if (request.getPays() != null) {
            equipe.setPays(request.getPays());
        }
        store.addLog("COMMISSAIRE update equipe id=" + id);
        return equipeRepository.save(equipe);
    }

    public void delete(Long id) {
        Equipe equipe = findByIdOrThrow(id);
        if (equipe.getAthletes() != null) {
            for (Athlete athlete : equipe.getAthletes()) {
                athlete.setEquipe(null);
            }
            athleteRepository.saveAll(equipe.getAthletes());
        }
        equipeRepository.deleteById(id);
        store.addLog("COMMISSAIRE delete equipe id=" + id);
    }

    public Equipe assignAthletes(Long equipeId, AssignAthletesRequest request) {
        Equipe equipe = findByIdOrThrow(equipeId);
        if (request == null || request.getAthleteIds() == null || request.getAthleteIds().isEmpty()) {
            throw new IllegalArgumentException("athleteIds est obligatoire");
        }
        List<Long> ids = request.getAthleteIds();
        for (Long id : ids) {
            if (id == null) {
                throw new IllegalArgumentException("athleteIds ne doit pas contenir de null");
            }
        }
        if (equipe.getAthletes() != null && !equipe.getAthletes().isEmpty()) {
            List<Long> existingIds = equipe.getAthletes().stream()
                    .map(Athlete::getId)
                    .collect(Collectors.toList());
            List<Long> alreadyInEquipe = ids.stream()
                    .filter(existingIds::contains)
                    .collect(Collectors.toList());
            if (!alreadyInEquipe.isEmpty()) {
                throw new IllegalArgumentException("athlete deja dans l'equipe: " + alreadyInEquipe);
            }
        }
        List<Athlete> athletes = athleteRepository.findAllById(ids);
        if (athletes.size() != ids.size()) {
            List<Long> foundIds = athletes.stream().map(Athlete::getId).collect(Collectors.toList());
            List<Long> missing = ids.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
            throw new IllegalArgumentException("Athlete(s) introuvable(s): " + missing);
        }
        for (Athlete athlete : athletes) {
            athlete.setEquipe(equipe);
        }
        athleteRepository.saveAll(athletes);
        Map<Long, Athlete> merged = new LinkedHashMap<>();
        if (equipe.getAthletes() != null) {
            for (Athlete existing : equipe.getAthletes()) {
                merged.put(existing.getId(), existing);
            }
        }
        for (Athlete athlete : athletes) {
            merged.put(athlete.getId(), athlete);
        }
        List<Athlete> mergedList = new ArrayList<>(merged.values());
        equipe.setAthletes(mergedList);
        store.addLog("COMMISSAIRE assign athletes equipeId=" + equipeId + " count=" + mergedList.size());
        return equipe;
    }
}
