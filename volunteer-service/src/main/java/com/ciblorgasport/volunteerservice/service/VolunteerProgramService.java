package com.ciblorgasport.volunteerservice.service;
// import com.ciblorgasport.volunteerservice.client.LieuClient;


import com.ciblorgasport.volunteerservice.dto.*;
import com.ciblorgasport.volunteerservice.entity.*;
import com.ciblorgasport.volunteerservice.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VolunteerProgramService {
    private static final Logger logger = LoggerFactory.getLogger(VolunteerProgramService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    // LieuClient removed
    private final VolunteerRepository volunteerRepository;
    private final VolunteerTaskRepository taskRepository;
    private final RestTemplate restTemplate;


    @Value("${auth-service.base-url:http://localhost:8081}")
    private String authServiceBaseUrl;

    public VolunteerProgramService(
            VolunteerRepository volunteerRepository,
            VolunteerTaskRepository taskRepository,
            RestTemplate restTemplate) {
        this.volunteerRepository = volunteerRepository;
        this.taskRepository = taskRepository;
        this.restTemplate = restTemplate;
    }
    // Conversion VolunteerTask -> DTO enrichi avec nom du lieu
    // toDto method removed

    // ========== GESTION DES VOLONTAIRES ==========
    
    @Transactional
    public Volunteer completeVolunteerProfile(
            VolunteerProfileDTO profileDTO,
            String authHeader) {
        AuthUserSummary authUser = resolveCurrentAuthUser(authHeader);
        logger.info("Completion du profil pour authUserId={}", authUser.id());

        Volunteer volunteer = volunteerRepository.findByAuthUserId(authUser.id())
                .orElse(new Volunteer());

        volunteer.setAuthUserId(authUser.id());
        volunteer.setFirstName(profileDTO.getFirstName());
        volunteer.setLastName(profileDTO.getLastName());
        volunteer.setEmail(authUser.email());
        volunteer.setPhoneNumber(profileDTO.getPhoneNumber());
        
        if (profileDTO.getLanguages() != null) {
            volunteer.setLanguagesFromSet(profileDTO.getLanguages());
        }
        
        if (profileDTO.getPreferredTaskTypes() != null) {
            volunteer.setPreferredTaskTypesFromSet(profileDTO.getPreferredTaskTypes());
        }

        volunteer.setActive(true);

        if (profileDTO.getAvailabilities() != null) {
            try {
                String availabilitiesJson = objectMapper.writeValueAsString(profileDTO.getAvailabilities());
                volunteer.setAvailabilitiesJson(availabilitiesJson);
            } catch (Exception e) {
                logger.error("Erreur lors de la conversion des disponibilités en JSON", e);
            }
        }

        Volunteer saved = volunteerRepository.save(volunteer);
        logger.info("Profil complete pour volontaire ID: {}", saved.getId());

        return saved;
    }

    public VolunteerProfileDTO completeVolunteerProfileDTO(
            VolunteerProfileDTO profileDTO,
            String authHeader) {
        Volunteer volunteer = completeVolunteerProfile(profileDTO, authHeader);
        return convertToProfileDTO(volunteer);
    }

    public Long getCurrentAuthUserId(String authHeader) {
        return resolveCurrentAuthUser(authHeader).id();
    }

    public VolunteerProfileDTO getVolunteerProfileDTO(Long authUserId) {
        logger.info("Recuperation du profil DTO pour authUserId={}", authUserId);

        Volunteer volunteer = volunteerRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Volontaire non trouve pour l'utilisateur " + authUserId
                ));

        return convertToProfileDTO(volunteer);
    }

    private VolunteerProfileDTO convertToProfileDTO(Volunteer volunteer) {
        Set<AvailabilityDTO> availabilityDTOs = new HashSet<>();
        if (volunteer.getAvailabilitiesJson() != null) {
            try {
                availabilityDTOs = objectMapper.readValue(
                    volunteer.getAvailabilitiesJson(),
                    new TypeReference<Set<AvailabilityDTO>>() {}
                );
            } catch (Exception e) {
                logger.error("Erreur lors de la lecture du JSON des disponibilités", e);
            }
        }

        boolean profileComplete = volunteer.getFirstName() != null && !volunteer.getFirstName().isEmpty() &&
                volunteer.getLastName() != null && !volunteer.getLastName().isEmpty();

        return new VolunteerProfileDTO(
                volunteer.getId(),
                volunteer.getAuthUserId(),
                volunteer.getEmail(),
                volunteer.getFirstName(),
                volunteer.getLastName(),
                volunteer.getPhoneNumber(),
                volunteer.getLanguagesSet(),
                volunteer.getPreferredTaskTypesSet(),
                availabilityDTOs,
                profileComplete
        );
    }

    public List<Volunteer> getAllVolunteers() {
        logger.info("Recuperation de tous les volontaires");
        return volunteerRepository.findAll();
    }

    public Volunteer getVolunteerById(UUID volunteerId) {
        logger.info("Recuperation du volontaire avec ID: {}", volunteerId);
        return volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Volontaire non trouve avec l'ID: " + volunteerId
                ));
    }

    // ========== GESTION DES TÂCHES ==========

    @Transactional
    public VolunteerTask createTask(VolunteerTaskDTO taskDTO, String authHeader) {
        logger.info("Creation d'une nouvelle tache: {}", taskDTO.getTitle());

        validateAdminAccess(authHeader);

        VolunteerTask task = new VolunteerTask();
        updateTaskFromDto(task, taskDTO);

        VolunteerTask saved = taskRepository.save(task);
        logger.info("Tache creee avec ID: {}", saved.getId());

        return saved;
    }

    @Transactional
    public List<VolunteerTask> importTasks(List<VolunteerTaskDTO> taskDTOs, String authHeader) {
        logger.info("Import de {} taches", taskDTOs.size());

        validateAdminAccess(authHeader);

        List<VolunteerTask> tasks = new ArrayList<>();
        for (VolunteerTaskDTO dto : taskDTOs) {
            VolunteerTask task = new VolunteerTask();
            updateTaskFromDto(task, dto);
            tasks.add(task);
        }

        List<VolunteerTask> saved = taskRepository.saveAll(tasks);
        logger.info("{} taches importees", saved.size());

        return saved;
    }

    private void updateTaskFromDto(VolunteerTask task, VolunteerTaskDTO dto) {
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setTaskDate(dto.getTaskDate());
        task.setStartTime(dto.getStartTime());
        task.setEndTime(dto.getEndTime());
        task.setLocationId(dto.getLocationId());
        task.setTaskType(dto.getTaskType());

        // ✅ Un seul volontaire assigné possible (on prend le premier du Set)
        if (dto.getAssignedVolunteerIds() != null && !dto.getAssignedVolunteerIds().isEmpty()) {
            task.setAssignedVolunteerIdsFromSet(dto.getAssignedVolunteerIds());
        }

        if (dto.getRequiredLanguages() != null) {
            task.setRequiredLanguagesFromSet(dto.getRequiredLanguages());
        }
    }

    @Transactional
    public VolunteerTask updateTask(UUID taskId, VolunteerTaskDTO taskDTO, String authHeader) {
        logger.info("Mise a jour de la tache: {}", taskId);

        validateAdminAccess(authHeader);

        VolunteerTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache non trouvee"));

        updateTaskFromDto(task, taskDTO);

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(UUID taskId, String authHeader) {
        logger.info("Suppression de la tache: {}", taskId);

        validateAdminAccess(authHeader);

        VolunteerTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache non trouvee"));

        taskRepository.delete(task);
        logger.info("Tache supprimee");
    }

    public List<VolunteerTask> getAllTasks() {
        logger.info("Recuperation de toutes les taches");
        return taskRepository.findAll();
    }

    public List<VolunteerTask> getTasksForDate(LocalDate date) {
        logger.info("Recuperation des taches pour le: {}", date);
        return taskRepository.findByTaskDate(date);
    }

    public List<VolunteerTask> getUpcomingTasks(LocalDate fromDate) {
        logger.info("Recuperation des taches a partir du: {}", fromDate);
        return taskRepository.findUpcomingTasks(fromDate);
    }

    // ========== MATCHING ET ASSIGNATION ==========

    public List<Volunteer> findSuitableVolunteers(UUID taskId) {
        logger.info("Recherche de volontaires pour la tache: {}", taskId);

        VolunteerTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache non trouvee"));

        List<Volunteer> allVolunteers = volunteerRepository.findAll();

        List<Volunteer> suitableVolunteers = allVolunteers.stream()
                .filter(Volunteer::isActive)
                .filter(v -> isVolunteerAvailable(v, task))
                .filter(v -> hasPreferredTaskType(v, task))
                .filter(v -> hasRequiredLanguages(v, task))
                .collect(Collectors.toList());

        logger.info("{} volontaires trouves avec les criteres", suitableVolunteers.size());

        return suitableVolunteers;
    }

    private boolean hasPreferredTaskType(Volunteer volunteer, VolunteerTask task) {
        Set<String> preferredTypes = volunteer.getPreferredTaskTypesSet();
        return preferredTypes.contains(task.getTaskType().toString());
    }

    private boolean hasRequiredLanguages(Volunteer volunteer, VolunteerTask task) {
        Set<String> requiredLanguages = getEffectiveRequiredLanguages(task);
        
        if (requiredLanguages.isEmpty()) {
            return true;
        }
        
        Set<String> volunteerLanguages = volunteer.getLanguagesSet();
        
        if (volunteerLanguages.isEmpty()) {
            return false;
        }
        
        Set<String> volunteerLower = volunteerLanguages.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        return requiredLanguages.stream()
                .map(String::toLowerCase)
                .allMatch(volunteerLower::contains);
    }

    private Set<String> getEffectiveRequiredLanguages(VolunteerTask task) {
        Set<String> requiredLanguages = task.getRequiredLanguagesSet();

        if (!requiredLanguages.isEmpty()) {
            return requiredLanguages;
        }

        if (task.getTaskType() == TaskType.SECURITE) {
            return Set.of("Français");
        }

        return Collections.emptySet();
    }

    public List<VolunteerMatchDTO> findVolunteersWithMatchInfo(UUID taskId) {
        logger.info("Recherche de tous les volontaires avec info de correspondance pour la tache: {}", taskId);

        VolunteerTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache non trouvee"));

        List<Volunteer> allVolunteers = volunteerRepository.findAll();

        return allVolunteers.stream()
                .map(volunteer -> {
                    VolunteerMatchDTO matchDTO = new VolunteerMatchDTO();
                    matchDTO.setVolunteer(volunteer);
                    
                    List<String> missingRequirements = new ArrayList<>();
                    
                    if (!volunteer.isActive()) {
                        missingRequirements.add("Volontaire non actif");
                    }
                    
                    if (!isVolunteerAvailable(volunteer, task)) {
                        missingRequirements.add("Pas disponible à ce moment");
                    }
                    
                    if (!hasPreferredTaskType(volunteer, task)) {
                        missingRequirements.add("Type de tâche non préféré (" + task.getTaskType() + ")");
                    }

                    if (hasScheduleConflict(volunteer.getId(), task)) {
                        missingRequirements.add("A déjà une tâche qui se chevauche");
                    }
                    
                    Set<String> requiredLanguages = getEffectiveRequiredLanguages(task);
                    if (!requiredLanguages.isEmpty()) {
                        Set<String> volunteerLanguages = volunteer.getLanguagesSet();
                        List<String> missingLanguages = requiredLanguages.stream()
                                .filter(lang -> !volunteerLanguages.contains(lang))
                                .collect(Collectors.toList());
                        
                        if (!missingLanguages.isEmpty()) {
                            missingRequirements.add("Langues manquantes: " + String.join(", ", missingLanguages));
                        }
                    }
                    
                    matchDTO.setMissingRequirements(missingRequirements);
                    matchDTO.setMatch(missingRequirements.isEmpty());
                    
                    return matchDTO;
                })
                .collect(Collectors.toList());
    }

    private boolean isVolunteerAvailable(Volunteer volunteer, VolunteerTask task) {
        if (volunteer.getAvailabilitiesJson() == null) {
            return false;
        }
        
        try {
            Set<AvailabilityDTO> availabilities = objectMapper.readValue(
                volunteer.getAvailabilitiesJson(),
                new TypeReference<Set<AvailabilityDTO>>() {}
            );
            
            DayOfWeek taskDay = task.getTaskDate().getDayOfWeek();
            String taskDayStr = taskDay.toString();
            
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String taskStartStr = task.getStartTime().format(timeFormatter);
            String taskEndStr = task.getEndTime().format(timeFormatter);
            
            return availabilities.stream().anyMatch(a -> 
                a.getDayOfWeek().equals(taskDayStr) &&
                a.getStartTime().compareTo(taskStartStr) <= 0 &&
                a.getEndTime().compareTo(taskEndStr) >= 0
            );
        } catch (Exception e) {
            logger.error("Erreur lors de la lecture des disponibilités", e);
            return false;
        }
    }

    private boolean hasScheduleConflict(UUID volunteerId, VolunteerTask newTask) {
        List<VolunteerTask> sameDayTasks = taskRepository.findByTaskDate(newTask.getTaskDate());

        return sameDayTasks.stream()
                .filter(existingTask -> !existingTask.getId().equals(newTask.getId()))
                .filter(existingTask -> existingTask.getAssignedVolunteerIdsSet().contains(volunteerId))
                .anyMatch(existingTask -> {
                    LocalTime existingStart = existingTask.getStartTime();
                    LocalTime existingEnd = existingTask.getEndTime();
                    LocalTime newStart = newTask.getStartTime();
                    LocalTime newEnd = newTask.getEndTime();

                    return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
                });
    }

    @Transactional
    public VolunteerTask assignVolunteer(UUID taskId, UUID volunteerId, String authHeader) {
        logger.info("Assignation du volontaire {} a la tache {}", volunteerId, taskId);

        validateAdminAccess(authHeader);

        VolunteerTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache non trouvee"));

        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Volontaire non trouve"));

        if (!volunteer.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le volontaire n'est pas actif");
        }

        // ✅ Vérifier si déjà assigné (un seul volontaire maximum)
        Set<UUID> assignedIds = task.getAssignedVolunteerIdsSet();
        if (!assignedIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un volontaire est deja assigne a cette tache");
        }

        if (!isVolunteerAvailable(volunteer, task)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le volontaire n'est pas disponible");
        }

        if (!hasRequiredLanguages(volunteer, task)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le volontaire ne parle pas les langues requises");
        }

        if (hasScheduleConflict(volunteerId, task)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Le volontaire a deja une tache qui se chevauche");
        }

        // ✅ Assignation (un seul volontaire)
        assignedIds.add(volunteerId);
        task.setAssignedVolunteerIdsFromSet(assignedIds);
        
        VolunteerTask saved = taskRepository.save(task);

        logger.info("Volontaire assigne avec succes a la tache {}", taskId);

        return saved;
    }

    @Transactional
    public VolunteerTask unassignVolunteer(UUID taskId, UUID volunteerId, String authHeader) {
        logger.info("Desassignation du volontaire {} de la tache {}", volunteerId, taskId);

        validateAdminAccess(authHeader);

        VolunteerTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache non trouvee"));

        Set<UUID> assignedIds = task.getAssignedVolunteerIdsSet();
        
        if (!assignedIds.contains(volunteerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Volontaire non assigne a cette tache");
        }

        assignedIds.remove(volunteerId);
        task.setAssignedVolunteerIdsFromSet(assignedIds);
        
        VolunteerTask saved = taskRepository.save(task);

        logger.info("Volontaire desassigne");
        return saved;
    }

    @Transactional
    public List<VolunteerTask> autoAssignVolunteers(UUID taskId, String authHeader) {
        logger.info("Auto-assignation pour la tache: {}", taskId);

        validateAdminAccess(authHeader);

        VolunteerTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache non trouvee"));

        // ✅ Vérifier si déjà assigné (un seul volontaire maximum)
        Set<UUID> assignedIds = task.getAssignedVolunteerIdsSet();
        if (!assignedIds.isEmpty()) {
            logger.info("Tache deja assignee a un volontaire");
            return Collections.singletonList(task);
        }

        List<Volunteer> suitableVolunteers = findSuitableVolunteers(taskId);
        
        if (suitableVolunteers.isEmpty()) {
            logger.info("Aucun volontaire disponible");
            return Collections.singletonList(task);
        }

        // ✅ Prendre le premier volontaire disponible
        UUID firstVolunteerId = suitableVolunteers.get(0).getId();
        assignedIds.add(firstVolunteerId);
        task.setAssignedVolunteerIdsFromSet(assignedIds);
        
        VolunteerTask saved = taskRepository.save(task);

        logger.info("1 volontaire auto-assigne a la tache {}", taskId);
        return Collections.singletonList(saved);
    }

    // ========== CONSULTATION VOLONTAIRE ==========

    public List<VolunteerTask> getVolunteerSchedule(Long authUserId, LocalDate date) {
        logger.info("Recuperation du planning pour volontaire authUserId={} date={}", authUserId, date);

        Volunteer volunteer = volunteerRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Volontaire non trouve"));

        LocalDate fromDate = date != null ? date : LocalDate.now();
        List<VolunteerTask> allTasks = taskRepository.findUpcomingTasks(fromDate);

        return allTasks.stream()
                .filter(task -> task.getAssignedVolunteerIdsSet().contains(volunteer.getId()))
                .sorted(Comparator.comparing(VolunteerTask::getTaskDate)
                        .thenComparing(VolunteerTask::getStartTime))
                .collect(Collectors.toList());
    }

    public List<VolunteerTask> getVolunteerTodaySchedule(Long authUserId) {
        return getVolunteerSchedule(authUserId, LocalDate.now());
    }

    // ========== UTILITAIRES AUTH ==========

    private AuthUserSummary resolveCurrentAuthUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token manquant ou invalide");
        }

        try {
            String url = authServiceBaseUrl + "/auth/me";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> payload = response.getBody();
            if (payload == null || payload.get("id") == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable");
            }

            Object idValue = payload.get("id");
            Long authUserId = idValue instanceof Number ? 
                ((Number) idValue).longValue() : 
                Long.parseLong(String.valueOf(idValue));

            String email = payload.get("email") != null ? String.valueOf(payload.get("email")) : null;
            return new AuthUserSummary(authUserId, email);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalide", e);
        } catch (HttpClientErrorException.Forbidden e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces refuse", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Auth-service indisponible", e);
        }
    }

    private void validateAdminAccess(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token manquant ou invalide");
        }

        try {
            String url = authServiceBaseUrl + "/auth/admin/volunteers?validated=true";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            restTemplate.exchange(url, HttpMethod.GET, entity, Void.class);
            logger.info("Acces admin valide");

        } catch (HttpClientErrorException.Forbidden e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces admin non autorise", e);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalide", e);
        } catch (Exception e) {
            logger.error("Erreur validation admin: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Service d'authentification indisponible", e);
        }
    }

    private record AuthUserSummary(Long id, String email) {}
}