package com.ciblorgasport.incidentservice.service;

import com.ciblorgasport.incidentservice.model.Incident;
import com.ciblorgasport.incidentservice.model.IncidentStatus;
import com.ciblorgasport.incidentservice.model.IncidentType;
import com.ciblorgasport.incidentservice.model.ImpactLevel;
import com.ciblorgasport.incidentservice.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentServiceImpl(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
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
    public List<Incident> search(IncidentStatus status, IncidentType type, ImpactLevel impact) {
        return incidentRepository.findByFilters(status, type, impact);
    }

    @Override
    public List<Incident> findByImpactLevel(ImpactLevel impactLevel) {
        return incidentRepository.findByImpactLevel(impactLevel);
    }

    @Override
    public List<Incident> findByStatus(IncidentStatus status) {
        return incidentRepository.findByStatus(status);
    }

    @Override
    public List<Incident> findByType(IncidentType type) {
        return incidentRepository.findByType(type);
    }

    @Override
    public Incident create(Incident incident) {
        incident.setReportedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());
        if (incident.getStatus() == null) {
            incident.setStatus(IncidentStatus.ACTIF);
        }
        return incidentRepository.save(incident);
    }

    @Override
    public Incident update(Long id, Incident incident) {
        return incidentRepository.findById(id)
                .map(existing -> {
                    existing.setDescription(incident.getDescription());
                    existing.setImpactLevel(incident.getImpactLevel());
                    existing.setType(incident.getType());
                    existing.setLocation(incident.getLocation());
                    existing.setStatus(incident.getStatus());
                    existing.setUpdatedAt(LocalDateTime.now());
                    
                    if (incident.getStatus() == IncidentStatus.RESOLU && existing.getResolvedAt() == null) {
                        existing.setResolvedAt(LocalDateTime.now());
                    }
                    
                    return incidentRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Incident not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        incidentRepository.deleteById(id);
    }
}
