package com.ciblorgasport.volunteerservice.controller;


import com.ciblorgasport.volunteerservice.dto.VolunteerProfileDTO;
// import com.ciblorgasport.volunteerservice.dto.VolunteerTaskDTO;
import com.ciblorgasport.volunteerservice.entity.VolunteerTask;
import com.ciblorgasport.volunteerservice.service.VolunteerProgramService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/volunteers")
public class VolunteerController {
    private final VolunteerProgramService service;

    public VolunteerController(VolunteerProgramService service) {
        this.service = service;
    }

    @PostMapping("/profile/complete")
    public ResponseEntity<VolunteerProfileDTO> completeProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody VolunteerProfileDTO profileDTO) {

        VolunteerProfileDTO completed = service.completeVolunteerProfileDTO(profileDTO, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(completed);
    }

    @GetMapping("/profile")
    public ResponseEntity<VolunteerProfileDTO> getMyProfile(
            @RequestHeader("Authorization") String authHeader) {
        Long authUserId = service.getCurrentAuthUserId(authHeader);
        VolunteerProfileDTO profile = service.getVolunteerProfileDTO(authUserId);
        return ResponseEntity.ok(profile);
    }


    @GetMapping("/schedule/today")
    public ResponseEntity<List<VolunteerTask>> getTodaySchedule(
            @RequestHeader("Authorization") String authHeader) {
        Long authUserId = service.getCurrentAuthUserId(authHeader);
        List<VolunteerTask> tasks = service.getVolunteerTodaySchedule(authUserId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<VolunteerTask>> getSchedule(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long authUserId = service.getCurrentAuthUserId(authHeader);
        List<VolunteerTask> tasks = service.getVolunteerSchedule(authUserId, date);
        return ResponseEntity.ok(tasks);
    }
}
