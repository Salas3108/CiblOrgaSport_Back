package com.ciblorgasport.volunteerservice.repository;

import com.ciblorgasport.volunteerservice.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, UUID> {
    Optional<Volunteer> findByAuthUserId(Long authUserId);
    List<Volunteer> findByActiveTrue();
    
    // Recherche par langue 
    @Query("SELECT v FROM Volunteer v WHERE v.active = true AND v.languages LIKE %:language%")
    List<Volunteer> findByLanguage(@Param("language") String language);
    
    // Recherche par type de tâche préféré
    @Query("SELECT v FROM Volunteer v WHERE v.active = true AND v.preferredTaskTypes LIKE %:taskType%")
    List<Volunteer> findByPreferredTaskType(@Param("taskType") String taskType);
}