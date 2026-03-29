package com.ciblorgasport.volunteerservice.controller;

import com.ciblorgasport.volunteerservice.dto.VolunteerMatchDTO;
import com.ciblorgasport.volunteerservice.dto.VolunteerTaskDTO;
import com.ciblorgasport.volunteerservice.entity.Volunteer;
import com.ciblorgasport.volunteerservice.entity.VolunteerTask;
import com.ciblorgasport.volunteerservice.service.VolunteerProgramService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/volunteers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVolunteerController {
    private final VolunteerProgramService service;

    public AdminVolunteerController(VolunteerProgramService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Volunteer>> getAllVolunteers() {
        return ResponseEntity.ok(service.getAllVolunteers());
    }

    @GetMapping("/{volunteerId}")
    public ResponseEntity<Volunteer> getVolunteer(@PathVariable UUID volunteerId) {
        Volunteer volunteer = service.getVolunteerById(volunteerId);
        return ResponseEntity.ok(volunteer);
    }

    @PostMapping("/tasks")
    public ResponseEntity<VolunteerTask> createTask(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody VolunteerTaskDTO taskDTO) {
        VolunteerTask task = service.createTask(taskDTO, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    // ✅ CORRECTION : Suppression du paramètre eventId
    @PostMapping("/tasks/import")
    public ResponseEntity<List<VolunteerTask>> importTasks(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody List<VolunteerTaskDTO> tasks) {
        List<VolunteerTask> saved = service.importTasks(tasks, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<VolunteerTask> updateTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID taskId,
            @Valid @RequestBody VolunteerTaskDTO taskDTO) {
        VolunteerTask task = service.updateTask(taskId, taskDTO, authHeader);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID taskId) {
        service.deleteTask(taskId, authHeader);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<VolunteerTask>> getAllTasks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date != null) {
            return ResponseEntity.ok(service.getTasksForDate(date));
        }
        return ResponseEntity.ok(service.getAllTasks());
    }

    @GetMapping("/tasks/{taskId}/suitable-volunteers")
    public ResponseEntity<List<Volunteer>> findSuitableVolunteers(@PathVariable UUID taskId) {
        List<Volunteer> volunteers = service.findSuitableVolunteers(taskId);
        return ResponseEntity.ok(volunteers);
    }

    @GetMapping("/tasks/{taskId}/volunteers-match-info")
    public ResponseEntity<List<VolunteerMatchDTO>> findVolunteersWithMatchInfo(@PathVariable UUID taskId) {
        List<VolunteerMatchDTO> matchInfo = service.findVolunteersWithMatchInfo(taskId);
        return ResponseEntity.ok(matchInfo);
    }

    @PostMapping("/tasks/{taskId}/assign/{volunteerId}")
    public ResponseEntity<VolunteerTask> assignVolunteer(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID taskId,
            @PathVariable UUID volunteerId) {
        VolunteerTask task = service.assignVolunteer(taskId, volunteerId, authHeader);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/tasks/{taskId}/assign/{volunteerId}")
    public ResponseEntity<VolunteerTask> unassignVolunteer(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID taskId,
            @PathVariable UUID volunteerId) {
        VolunteerTask task = service.unassignVolunteer(taskId, volunteerId, authHeader);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/tasks/{taskId}/auto-assign")
    public ResponseEntity<List<VolunteerTask>> autoAssign(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID taskId) {
        List<VolunteerTask> tasks = service.autoAssignVolunteers(taskId, authHeader);
        return ResponseEntity.ok(tasks);
    }
}