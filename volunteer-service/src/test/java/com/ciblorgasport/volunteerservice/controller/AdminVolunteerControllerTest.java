package com.ciblorgasport.volunteerservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import com.ciblorgasport.volunteerservice.dto.VolunteerTaskDTO;
import com.ciblorgasport.volunteerservice.entity.Volunteer;
import com.ciblorgasport.volunteerservice.entity.VolunteerTask;
import com.ciblorgasport.volunteerservice.service.VolunteerProgramService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminVolunteerControllerTest {

    @Mock
    private VolunteerProgramService service;

    @InjectMocks
    private AdminVolunteerController controller;

    private static final String AUTH_HEADER = "Bearer test-token";

    @Test
    void getAllVolunteers_returns_list() {
        Volunteer v = new Volunteer();
        v.setId(UUID.randomUUID());
        when(service.getAllVolunteers()).thenReturn(List.of(v));

        ResponseEntity<List<Volunteer>> response = controller.getAllVolunteers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getVolunteer_returns_volunteer() {
        UUID id = UUID.randomUUID();
        Volunteer v = new Volunteer();
        v.setId(id);
        when(service.getVolunteerById(id)).thenReturn(v);

        ResponseEntity<Volunteer> response = controller.getVolunteer(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(id, response.getBody().getId());
    }

    @Test
    void createTask_returns_201() {
        VolunteerTaskDTO dto = new VolunteerTaskDTO("title", "desc", null, null, null, null, null, null, null);
        VolunteerTask task = new VolunteerTask();
        task.setId(UUID.randomUUID());
        when(service.createTask(any(VolunteerTaskDTO.class), eq(AUTH_HEADER))).thenReturn(task);

        ResponseEntity<VolunteerTask> response = controller.createTask(AUTH_HEADER, dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(task.getId(), response.getBody().getId());
    }

    @Test
    void deleteTask_returns_204() {
        UUID taskId = UUID.randomUUID();
        doNothing().when(service).deleteTask(eq(taskId), eq(AUTH_HEADER));

        ResponseEntity<Void> response = controller.deleteTask(AUTH_HEADER, taskId);

        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void getAllTasks_without_date_returns_all() {
        VolunteerTask task = new VolunteerTask();
        task.setId(UUID.randomUUID());
        when(service.getAllTasks()).thenReturn(List.of(task));

        ResponseEntity<List<VolunteerTask>> response = controller.getAllTasks(null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void findSuitableVolunteers_returns_list() {
        UUID taskId = UUID.randomUUID();
        Volunteer v = new Volunteer();
        v.setId(UUID.randomUUID());
        when(service.findSuitableVolunteers(taskId)).thenReturn(List.of(v));

        ResponseEntity<List<Volunteer>> response = controller.findSuitableVolunteers(taskId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void assignVolunteer_returns_task() {
        UUID taskId = UUID.randomUUID();
        UUID volunteerId = UUID.randomUUID();
        VolunteerTask task = new VolunteerTask();
        task.setId(taskId);
        when(service.assignVolunteer(taskId, volunteerId, AUTH_HEADER)).thenReturn(task);

        ResponseEntity<VolunteerTask> response = controller.assignVolunteer(AUTH_HEADER, taskId, volunteerId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(taskId, response.getBody().getId());
    }
}
