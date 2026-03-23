package com.ciblorgasport.incidentservice.service.impl;

import com.ciblorgasport.incidentservice.model.Incident;
import com.ciblorgasport.incidentservice.model.IncidentStatus;
import com.ciblorgasport.incidentservice.model.IncidentType;
import com.ciblorgasport.incidentservice.model.ImpactLevel;
import com.ciblorgasport.incidentservice.kafka.publisher.IncidentEventFactory;
import com.ciblorgasport.incidentservice.kafka.publisher.IncidentEventPublisher;
import com.ciblorgasport.incidentservice.repository.IncidentRepository;
import com.ciblorgasport.incidentservice.service.IncidentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentEventPublisher incidentEventPublisher;

    public IncidentServiceImpl(IncidentRepository incidentRepository, IncidentEventPublisher incidentEventPublisher) {
        this.incidentRepository = incidentRepository;
        this.incidentEventPublisher = incidentEventPublisher;
    }

    @Override
    public List<Incident> findAll() {
        return incidentRepository.findAll();
    }

    @Override
    public Optional<Incident> findById(Long id) {
        return incidentRepository.findById(id);
    }

    @Override
    public Incident create(Incident incident) {
        if (incident.getReportedAt() == null) {
            incident.setReportedAt(LocalDateTime.now());
        }
        if (incident.getStatus() == null) {
            incident.setStatus(IncidentStatus.ACTIF);
        }
        Incident saved = incidentRepository.save(incident);
        incidentEventPublisher.publishIncidentCreated(
                IncidentEventFactory.incidentCreated(saved),
                IncidentEventFactory.partitionKey(saved)
        );
        return saved;
    }

    @Override
    public Incident update(Long id, Incident incident) {
        return incidentRepository.findById(id).map(existing -> {
            existing.setDescription(incident.getDescription());
            existing.setImpactLevel(incident.getImpactLevel());
            existing.setType(incident.getType());
            existing.setLieuId(incident.getLieuId());
            existing.setStatus(incident.getStatus());
            existing.setReportedBy(incident.getReportedBy());

            existing.setUpdatedAt(LocalDateTime.now());
            if (existing.getStatus() == IncidentStatus.RESOLU && existing.getResolvedAt() == null) {
                existing.setResolvedAt(LocalDateTime.now());
            }

            return incidentRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Incident not found: " + id));
    }

    @Override
    public void delete(Long id) {
        incidentRepository.deleteById(id);
    }

    @Override
    public List<Incident> findByStatus(IncidentStatus status) {
        return incidentRepository.findByStatus(status);
    }

    @Override
    public List<Incident> findByImpactLevel(ImpactLevel impactLevel) {
        return incidentRepository.findByImpactLevel(impactLevel);
    }

    @Override
    public List<Incident> findByType(IncidentType type) {
        return incidentRepository.findByType(type);
    }

    @Override
    public List<Incident> search(IncidentStatus status, IncidentType type, ImpactLevel impactLevel) {
        List<Incident> all = incidentRepository.findAll();
        return all.stream()
                .filter(i -> (status == null || i.getStatus() == status))
                .filter(i -> (type == null || i.getType() == type))
                .filter(i -> (impactLevel == null || i.getImpactLevel() == impactLevel))
                .collect(Collectors.toList());
    }
}
