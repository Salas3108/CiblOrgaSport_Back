package com.ciblorgasport.volunteerservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ciblorgasport.volunteerservice.entity.VolunteerProgramTask;

@Repository
public interface VolunteerProgramTaskRepository extends JpaRepository<VolunteerProgramTask, UUID> {
    List<VolunteerProgramTask> findByVolunteerIdAndTaskDate(Long volunteerId, LocalDate taskDate);
    
    @Query("SELECT t FROM VolunteerProgramTask t WHERE t.volunteerId = :volunteerId AND t.taskDate >= :fromDate ORDER BY t.taskDate ASC, t.startTime ASC")
    List<VolunteerProgramTask> findUpcomingTasks(@Param("volunteerId") Long volunteerId, @Param("fromDate") LocalDate fromDate);
}
