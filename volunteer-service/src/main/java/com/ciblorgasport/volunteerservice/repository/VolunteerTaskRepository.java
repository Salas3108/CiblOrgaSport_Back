package com.ciblorgasport.volunteerservice.repository;

import com.ciblorgasport.volunteerservice.entity.VolunteerTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface VolunteerTaskRepository extends JpaRepository<VolunteerTask, UUID> {
    
    List<VolunteerTask> findByTaskDate(LocalDate date);

    @Query("SELECT t FROM VolunteerTask t WHERE t.taskDate >= :fromDate ORDER BY t.taskDate ASC, t.startTime ASC")
    List<VolunteerTask> findUpcomingTasks(@Param("fromDate") LocalDate fromDate);
}