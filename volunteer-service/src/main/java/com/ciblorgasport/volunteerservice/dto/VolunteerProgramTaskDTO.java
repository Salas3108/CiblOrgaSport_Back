package com.ciblorgasport.volunteerservice.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VolunteerProgramTaskDTO {
    @NotNull
    private Long volunteerId;

    @NotBlank
    private String volunteerName;

    @NotNull
    private LocalDate taskDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotBlank
    private String location;

    @NotBlank
    private String role;

    public VolunteerProgramTaskDTO() {
    }

    public VolunteerProgramTaskDTO(Long volunteerId, String volunteerName, LocalDate taskDate,
                                   LocalTime startTime, LocalTime endTime, String location, String role) {
        this.volunteerId = volunteerId;
        this.volunteerName = volunteerName;
        this.taskDate = taskDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.role = role;
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
