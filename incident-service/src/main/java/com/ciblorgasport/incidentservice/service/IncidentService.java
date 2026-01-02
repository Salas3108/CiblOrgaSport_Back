package com.ciblorgasport.incidentservice.service;

import com.ciblorgasport.incidentservice.model.Incident;
import com.ciblorgasport.incidentservice.model.IncidentStatus;
import com.ciblorgasport.incidentservice.model.IncidentType;
import com.ciblorgasport.incidentservice.model.ImpactLevel;

import java.util.List;
import java.util.Optional;

public interface IncidentService {
    
    List<Incident> findAll();
    
    Optional<Incident> findById(Long id);
    
    Incident create(Incident incident);
    
    Incident update(Long id, Incident incident);
    
    void delete(Long id);
    
    List<Incident> findByStatus(IncidentStatus status);
    
    List<Incident> findByImpactLevel(ImpactLevel impactLevel);
    
    List<Incident> findByType(IncidentType type);
    
    List<Incident> search(IncidentStatus status, IncidentType type, ImpactLevel impactLevel);
}
