package com.ciblorgasport.volunteerservice.dto;

import com.ciblorgasport.volunteerservice.entity.TaskType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public class VolunteerTaskDTO {
    // Champs pour la réponse
    private UUID id;
    
    // Champs obligatoires
    private String title;
    
    private String description;
    
    private LocalDate taskDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String location;
    
    private TaskType taskType;
    
    // Champs optionnels
    private Set<UUID> assignedVolunteerIds;  // Un seul volontaire max
    private Set<String> requiredLanguages;
    
    // Champs calculés pour la réponse
    private boolean assigned;

    // Constructeur pour la création (sans id)
    @JsonCreator
    public VolunteerTaskDTO(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("taskDate") LocalDate taskDate,
            @JsonProperty("startTime") LocalTime startTime,
            @JsonProperty("endTime") LocalTime endTime,
            @JsonProperty("location") String location,
            @JsonProperty("taskType") TaskType taskType,
            @JsonProperty("requiredLanguages") Set<String> requiredLanguages) {
        this.title = title;
        this.description = description;
        this.taskDate = taskDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.taskType = taskType;
        this.requiredLanguages = requiredLanguages;
    }

    // Constructeur complet pour la réponse
    public VolunteerTaskDTO(UUID id, String title, String description, LocalDate taskDate,
                           LocalTime startTime, LocalTime endTime, String location,
                           TaskType taskType, Set<UUID> assignedVolunteerIds, 
                           Set<String> requiredLanguages, boolean assigned) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskDate = taskDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.taskType = taskType;
        this.assignedVolunteerIds = assignedVolunteerIds;
        this.requiredLanguages = requiredLanguages;
        this.assigned = assigned;
    }

    // Getters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getTaskDate() { return taskDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public TaskType getTaskType() { return taskType; }
    public Set<UUID> getAssignedVolunteerIds() { return assignedVolunteerIds; }
    public Set<String> getRequiredLanguages() { return requiredLanguages; }
    public boolean isAssigned() { return assigned; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setTaskDate(LocalDate taskDate) { this.taskDate = taskDate; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public void setLocation(String location) { this.location = location; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    public void setAssignedVolunteerIds(Set<UUID> assignedVolunteerIds) { 
        this.assignedVolunteerIds = assignedVolunteerIds; 
    }
    public void setRequiredLanguages(Set<String> requiredLanguages) { 
        this.requiredLanguages = requiredLanguages; 
    }
    public void setAssigned(boolean assigned) { this.assigned = assigned; }
}