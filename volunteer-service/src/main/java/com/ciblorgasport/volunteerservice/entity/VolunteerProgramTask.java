package com.ciblorgasport.volunteerservice.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "volunteer_program_tasks")
public class VolunteerProgramTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "volunteer_id", nullable = false)
    private Long volunteerId;

    @NotBlank
    @Column(name = "volunteer_name", nullable = false, length = 100)
    private String volunteerName;

    @NotNull
    @Column(name = "task_date", nullable = false)
    private LocalDate taskDate;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String location;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String role;

    public VolunteerProgramTask() {
    }

    public VolunteerProgramTask(Long volunteerId, String volunteerName, LocalDate taskDate,
                                LocalTime startTime, LocalTime endTime, String location, String role) {
        this.volunteerId = volunteerId;
        this.volunteerName = volunteerName;
        this.taskDate = taskDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public void setVolunteerName(String volunteerName) {
        this.volunteerName = volunteerName;
    }

    public LocalDate getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(LocalDate taskDate) {
        this.taskDate = taskDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
