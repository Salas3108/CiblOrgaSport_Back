package com.ciblorgasport.volunteerservice.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.volunteerservice.dto.VolunteerProgramTaskDTO;
import com.ciblorgasport.volunteerservice.service.VolunteerProgramService;

@RestController
@RequestMapping("/api/v1/volunteers")
public class VolunteerProgramController {
    private final VolunteerProgramService service;

    public VolunteerProgramController(VolunteerProgramService service) {
        this.service = service;
    }

    @GetMapping("/{volunteerId}/today")
    public ResponseEntity<List<VolunteerProgramTaskDTO>> getTodayProgram(
            @PathVariable Long volunteerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        // Récupérer toutes les tâches à partir de la date spécifiée (ou aujourd'hui)
        return ResponseEntity.ok(service.getUpcomingTasks(volunteerId, targetDate));
    }

    @GetMapping("/{volunteerId}/debug")
    public ResponseEntity<Map<String, Object>> debugTasks(
            @PathVariable Long volunteerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<VolunteerProgramTaskDTO> tasks = service.getUpcomingTasks(volunteerId, targetDate);
        return ResponseEntity.ok(Map.of(
            "volunteerId", volunteerId,
            "fromDate", targetDate,
            "taskCount", tasks.size(),
            "tasks", tasks
        ));
    }
}

