package com.ciblorgasport.volunteerservice.service;

import com.ciblorgasport.volunteerservice.dto.AvailabilityDTO;
import com.ciblorgasport.volunteerservice.dto.VolunteerProfileDTO;
import com.ciblorgasport.volunteerservice.entity.TaskType;
import com.ciblorgasport.volunteerservice.entity.Volunteer;
import com.ciblorgasport.volunteerservice.entity.VolunteerTask;
import com.ciblorgasport.volunteerservice.repository.VolunteerRepository;
import com.ciblorgasport.volunteerservice.repository.VolunteerTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VolunteerProgramServiceTest {

    private static final String AUTH_HEADER = "Bearer valid-token";
    private static final String AUTH_BASE_URL = "http://auth-service";

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private VolunteerTaskRepository taskRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VolunteerProgramService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "authServiceBaseUrl", AUTH_BASE_URL);
    }

    @Test
    void completeVolunteerProfileDTO_createsProfileFromAuthUser() {
        stubCurrentUser(42L, "alice@example.com");

        VolunteerProfileDTO profileDTO = new VolunteerProfileDTO(
                "Alice",
                "Martin",
                null,
                "0601020304",
                Set.of("Français", "English"),
                Set.of("SECURITE", "ACCUEIL"),
                Set.of(new AvailabilityDTO("MONDAY", "08:00", "18:00"))
        );

        when(volunteerRepository.findByAuthUserId(42L)).thenReturn(Optional.empty());
        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
            Volunteer volunteer = invocation.getArgument(0);
            volunteer.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            return volunteer;
        });

        VolunteerProfileDTO result = service.completeVolunteerProfileDTO(profileDTO, AUTH_HEADER);

        assertNotNull(result.getId());
        assertEquals(42L, result.getAuthUserId());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals("Alice", result.getFirstName());
        assertEquals("Martin", result.getLastName());
        assertEquals(Set.of("Français", "English"), result.getLanguages());
        assertEquals(Set.of("SECURITE", "ACCUEIL"), result.getPreferredTaskTypes());
        assertEquals(1, result.getAvailabilities().size());
        assertTrue(result.isProfileComplete());

        ArgumentCaptor<Volunteer> savedVolunteerCaptor = ArgumentCaptor.forClass(Volunteer.class);
        verify(volunteerRepository).save(savedVolunteerCaptor.capture());
        assertEquals("alice@example.com", savedVolunteerCaptor.getValue().getEmail());
        assertEquals(42L, savedVolunteerCaptor.getValue().getAuthUserId());
        assertTrue(savedVolunteerCaptor.getValue().isActive());
        assertNotNull(savedVolunteerCaptor.getValue().getAvailabilitiesJson());
    }

    @Test
    void findSuitableVolunteers_returnsOnlyEligibleVolunteers() {
        UUID taskId = UUID.randomUUID();
        VolunteerTask task = buildTask(taskId, LocalDate.of(2026, 3, 16), LocalTime.of(9, 0), LocalTime.of(12, 0), TaskType.SECURITE);

        Volunteer eligible = buildVolunteer(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                true,
                Set.of("SECURITE"),
                Set.of("Français", "English"),
                availabilityJson("MONDAY", "08:00", "18:00")
        );

        Volunteer inactive = buildVolunteer(
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                false,
                Set.of("SECURITE"),
                Set.of("Français"),
                availabilityJson("MONDAY", "08:00", "18:00")
        );

        Volunteer unavailable = buildVolunteer(
                UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
                true,
                Set.of("SECURITE"),
                Set.of("Français"),
                availabilityJson("TUESDAY", "08:00", "18:00")
        );

        Volunteer wrongPreference = buildVolunteer(
                UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
                true,
                Set.of("ACCUEIL"),
                Set.of("Français"),
                availabilityJson("MONDAY", "08:00", "18:00")
        );

        Volunteer wrongLanguage = buildVolunteer(
                UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee"),
                true,
                Set.of("SECURITE"),
                Set.of("English"),
                availabilityJson("MONDAY", "08:00", "18:00")
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(volunteerRepository.findAll()).thenReturn(List.of(eligible, inactive, unavailable, wrongPreference, wrongLanguage));

        List<Volunteer> result = service.findSuitableVolunteers(taskId);

        assertEquals(1, result.size());
        assertEquals(eligible.getId(), result.get(0).getId());
    }

    @Test
    void assignVolunteer_assignsVolunteerWhenEligible() {
        stubAdminAccess();

        UUID taskId = UUID.randomUUID();
        UUID volunteerId = UUID.randomUUID();
        VolunteerTask task = buildTask(taskId, LocalDate.of(2026, 3, 16), LocalTime.of(10, 0), LocalTime.of(12, 0), TaskType.SECURITE);
        Volunteer volunteer = buildVolunteer(
                volunteerId,
                true,
                Set.of("SECURITE"),
                Set.of("Français"),
                availabilityJson("MONDAY", "08:00", "18:00")
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));
        when(taskRepository.findByTaskDate(task.getTaskDate())).thenReturn(List.of(task));
        when(taskRepository.save(any(VolunteerTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VolunteerTask result = service.assignVolunteer(taskId, volunteerId, AUTH_HEADER);

        assertTrue(result.getAssignedVolunteerIdsSet().contains(volunteerId));
        verify(taskRepository).save(task);
    }

    @Test
    void assignVolunteer_rejectsOverlappingSchedule() {
        stubAdminAccess();

        UUID taskId = UUID.randomUUID();
        UUID volunteerId = UUID.randomUUID();
        VolunteerTask targetTask = buildTask(taskId, LocalDate.of(2026, 3, 16), LocalTime.of(11, 0), LocalTime.of(13, 0), TaskType.SECURITE);
        Volunteer volunteer = buildVolunteer(
                volunteerId,
                true,
                Set.of("SECURITE"),
                Set.of("Français"),
                availabilityJson("MONDAY", "08:00", "18:00")
        );

        VolunteerTask existingTask = buildTask(UUID.randomUUID(), LocalDate.of(2026, 3, 16), LocalTime.of(9, 30), LocalTime.of(12, 0), TaskType.SECURITE);
        existingTask.assignVolunteer(volunteerId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(targetTask));
        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));
        when(taskRepository.findByTaskDate(targetTask.getTaskDate())).thenReturn(List.of(targetTask, existingTask));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.assignVolunteer(taskId, volunteerId, AUTH_HEADER)
        );

        assertEquals(409, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("chevauche"));
        verify(taskRepository, never()).save(any(VolunteerTask.class));
    }

    @Test
    void getVolunteerSchedule_returnsOnlyAssignedTasksSortedByDateAndTime() {
        UUID volunteerId = UUID.randomUUID();
        Volunteer volunteer = buildVolunteer(
                volunteerId,
                true,
                Set.of("ACCUEIL"),
                Set.of("Français"),
                availabilityJson("MONDAY", "08:00", "18:00")
        );
        volunteer.setAuthUserId(77L);

        VolunteerTask lateTask = buildTask(UUID.randomUUID(), LocalDate.of(2026, 3, 18), LocalTime.of(14, 0), LocalTime.of(16, 0), TaskType.ACCUEIL);
        lateTask.assignVolunteer(volunteerId);

        VolunteerTask earlyTask = buildTask(UUID.randomUUID(), LocalDate.of(2026, 3, 17), LocalTime.of(9, 0), LocalTime.of(10, 0), TaskType.ACCUEIL);
        earlyTask.assignVolunteer(volunteerId);

        VolunteerTask foreignTask = buildTask(UUID.randomUUID(), LocalDate.of(2026, 3, 17), LocalTime.of(8, 0), LocalTime.of(9, 0), TaskType.ACCUEIL);
        foreignTask.assignVolunteer(UUID.randomUUID());

        when(volunteerRepository.findByAuthUserId(77L)).thenReturn(Optional.of(volunteer));
        when(taskRepository.findUpcomingTasks(LocalDate.of(2026, 3, 17))).thenReturn(List.of(lateTask, foreignTask, earlyTask));

        List<VolunteerTask> result = service.getVolunteerSchedule(77L, LocalDate.of(2026, 3, 17));

        assertEquals(2, result.size());
        assertEquals(earlyTask.getId(), result.get(0).getId());
        assertEquals(lateTask.getId(), result.get(1).getId());
    }

    @Test
    void getCurrentAuthUserId_withoutBearerToken_throwsUnauthorized() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getCurrentAuthUserId("invalid-token")
        );

        assertEquals(401, exception.getStatusCode().value());
        assertEquals("Token manquant ou invalide", exception.getReason());
    }

    private void stubCurrentUser(Long id, String email) {
        String url = AUTH_BASE_URL + "/auth/me";
        Map<String, Object> payload = Map.of("id", id, "email", email);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(payload));
    }

    private void stubAdminAccess() {
        String url = AUTH_BASE_URL + "/auth/admin/volunteers?validated=true";
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(ResponseEntity.ok().build());
    }

    private Volunteer buildVolunteer(UUID volunteerId, boolean active, Set<String> preferredTypes, Set<String> languages, String availabilitiesJson) {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(volunteerId);
        volunteer.setActive(active);
        volunteer.setFirstName("Test");
        volunteer.setLastName("Volunteer");
        volunteer.setEmail("volunteer@example.com");
        volunteer.setPreferredTaskTypesFromSet(preferredTypes);
        volunteer.setLanguagesFromSet(languages);
        volunteer.setAvailabilitiesJson(availabilitiesJson);
        return volunteer;
    }

    private VolunteerTask buildTask(UUID taskId, LocalDate date, LocalTime start, LocalTime end, TaskType taskType) {
        VolunteerTask task = new VolunteerTask();
        task.setId(taskId);
        task.setTitle("Mission");
        task.setDescription("Description");
        task.setTaskDate(date);
        task.setStartTime(start);
        task.setEndTime(end);
        task.setLocation("Stade");
        task.setTaskType(taskType);
        return task;
    }

    private String availabilityJson(String dayOfWeek, String startTime, String endTime) {
        return String.format(
                "[{\"dayOfWeek\":\"%s\",\"startTime\":\"%s\",\"endTime\":\"%s\"}]",
                dayOfWeek,
                startTime,
                endTime
        );
    }
}