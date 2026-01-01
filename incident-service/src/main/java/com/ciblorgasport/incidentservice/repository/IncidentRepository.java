package com.ciblorgasport.incidentservice.repository;

import com.ciblorgasport.incidentservice.model.Incident;
import com.ciblorgasport.incidentservice.model.IncidentStatus;
import com.ciblorgasport.incidentservice.model.IncidentType;
import com.ciblorgasport.incidentservice.model.ImpactLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatus(IncidentStatus status);
    List<Incident> findByImpactLevel(ImpactLevel impactLevel);
    List<Incident> findByType(IncidentType type);

    @Query("SELECT i FROM IncidentModel i WHERE " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:type IS NULL OR i.type = :type) AND " +
           "(:impact IS NULL OR i.impactLevel = :impact)")
    List<Incident> findByFilters(@Param("status") IncidentStatus status,
                                @Param("type") IncidentType type,
                                @Param("impact") ImpactLevel impact);
}
