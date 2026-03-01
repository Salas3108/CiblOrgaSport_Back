package com.ciblorgasport.volunteerservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.volunteerservice.dto.VolunteerProgramTaskDTO;
import com.ciblorgasport.volunteerservice.service.VolunteerProgramService;

@RestController
@RequestMapping("/api/v1/admin/programs")
public class AdminProgramController {
    private final VolunteerProgramService service;

    public AdminProgramController(VolunteerProgramService service) {
        this.service = service;
    }

    @PostMapping("/import")
    public ResponseEntity<List<VolunteerProgramTaskDTO>> importPrograms(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Validated @RequestBody List<VolunteerProgramTaskDTO> tasks) {
        List<VolunteerProgramTaskDTO> saved = service.importPrograms(tasks, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/all")
    public ResponseEntity<List<VolunteerProgramTaskDTO>> getAllTasks() {
        List<VolunteerProgramTaskDTO> tasks = service.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
}
