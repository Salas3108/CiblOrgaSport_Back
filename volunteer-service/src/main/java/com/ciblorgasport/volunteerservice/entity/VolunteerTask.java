package com.ciblorgasport.volunteerservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "volunteer_tasks")
public class VolunteerTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private LocalDate taskDate;
    
    @Column(nullable = false)
    private LocalTime startTime;
    
    @Column(nullable = false)
    private LocalTime endTime;
    
    @Column(nullable = false)
    private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    private String assignedVolunteerIds;

    private String requiredLanguages;
    
    // Constructeurs
    public VolunteerTask() {}

    public VolunteerTask(String title, String description, LocalDate taskDate, 
                        LocalTime startTime, LocalTime endTime, String location, 
                        TaskType taskType) {
        this.title = title;
        this.description = description;
        this.taskDate = taskDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.taskType = taskType;
    }

    // Getters et setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getTaskDate() { return taskDate; }
    public void setTaskDate(LocalDate taskDate) { this.taskDate = taskDate; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    
    public String getAssignedVolunteerIds() { return assignedVolunteerIds; }
    public void setAssignedVolunteerIds(String assignedVolunteerIds) { this.assignedVolunteerIds = assignedVolunteerIds; }
    
    public String getRequiredLanguages() { return requiredLanguages; }
    public void setRequiredLanguages(String requiredLanguages) { this.requiredLanguages = requiredLanguages; }
    
    // Méthodes utilitaires
    public Set<UUID> getAssignedVolunteerIdsSet() {
        if (assignedVolunteerIds == null || assignedVolunteerIds.isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(assignedVolunteerIds.split(","))
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }
    
    public void setAssignedVolunteerIdsFromSet(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            this.assignedVolunteerIds = null;
        } else {
            this.assignedVolunteerIds = ids.stream()
                    .map(UUID::toString)
                    .collect(Collectors.joining(","));
        }
    }
    
    public Set<String> getRequiredLanguagesSet() {
        if (requiredLanguages == null || requiredLanguages.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(requiredLanguages.split(",")));
    }
    
    public void setRequiredLanguagesFromSet(Set<String> langs) {
        if (langs == null || langs.isEmpty()) {
            this.requiredLanguages = null;
        } else {
            this.requiredLanguages = String.join(",", langs);
        }
    }
    
    public int getAssignedCount() {
        Set<UUID> assigned = getAssignedVolunteerIdsSet();
        return assigned.size();
    }
    
    public boolean isAssigned() {
        return assignedVolunteerIds != null && !assignedVolunteerIds.isEmpty();
    }
    
    public UUID getAssignedVolunteerId() {
        Set<UUID> assigned = getAssignedVolunteerIdsSet();
        return assigned.isEmpty() ? null : assigned.iterator().next();
    }
    
    public void assignVolunteer(UUID volunteerId) {
        Set<UUID> assigned = new HashSet<>();
        assigned.add(volunteerId);
        setAssignedVolunteerIdsFromSet(assigned);
    }
    
    public void unassignVolunteer() {
        setAssignedVolunteerIdsFromSet(new HashSet<>());
    }
}