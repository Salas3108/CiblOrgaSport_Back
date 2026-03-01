package com.ciblorgasport.volunteerservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciblorgasport.volunteerservice.dto.VolunteerProgramTaskDTO;
import com.ciblorgasport.volunteerservice.entity.VolunteerProgramTask;
import com.ciblorgasport.volunteerservice.repository.VolunteerProgramTaskRepository;

@Service
public class VolunteerProgramService {
    private static final Logger logger = LoggerFactory.getLogger(VolunteerProgramService.class);
    private final VolunteerProgramTaskRepository repository;
    private final RestTemplate restTemplate;
    
    @Value("${auth-service.base-url:http://localhost:8081}")
    private String authServiceBaseUrl;

    public VolunteerProgramService(VolunteerProgramTaskRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public List<VolunteerProgramTaskDTO> getDailyProgram(Long volunteerId, LocalDate date) {
        logger.info("Récupération du programme pour volunteer={} date={}", volunteerId, date);
        List<VolunteerProgramTask> tasks = repository.findByVolunteerIdAndTaskDate(volunteerId, date);
        logger.info("Nombre de tâches trouvées: {}", tasks.size());
        return tasks.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public List<VolunteerProgramTaskDTO> getUpcomingTasks(Long volunteerId, LocalDate fromDate) {
        logger.info("Récupération de toutes les tâches à venir pour volunteer={} depuis={}", volunteerId, fromDate);
        List<VolunteerProgramTask> tasks = repository.findUpcomingTasks(volunteerId, fromDate);
        logger.info("Nombre de tâches trouvées: {}", tasks.size());
        return tasks.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public List<VolunteerProgramTaskDTO> getAllTasks() {
        logger.info("Récupération de toutes les tâches");
        List<VolunteerProgramTask> tasks = repository.findAll();
        logger.info("Nombre total de tâches: {}", tasks.size());
        return tasks.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public List<VolunteerProgramTaskDTO> importPrograms(List<VolunteerProgramTaskDTO> tasks, String authHeader) {
        logger.info("Import de {} tâche(s)", tasks.size());
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                Set<Long> volunteerIds = fetchVolunteerIds(authHeader);
                tasks.forEach(task -> validateVolunteerExists(task.getVolunteerId(), volunteerIds));
                logger.info("Vérification auth-service réussie");
            } catch (Exception e) {
                logger.warn("Erreur de vérification mais on continue: {}", e.getMessage());
            }
        } else {
            logger.warn("Pas de token valide, import sans vérification");
        }
        
        List<VolunteerProgramTask> entities = tasks.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());

        List<VolunteerProgramTask> saved = repository.saveAll(entities);
        logger.info("Tâches sauvegardées: {}", saved.size());
        
        return saved.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private Set<Long> fetchVolunteerIds(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT manquant ou invalide");
        }

        String url = authServiceBaseUrl + "/auth/admin/volunteers";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
            List<java.util.Map<String, Object>> volunteers = response.getBody();
            if (volunteers == null) {
                return Set.of();
            }
            return volunteers.stream()
                .map(v -> v.get("id"))
                .filter(id -> id != null)
                .map(id -> Long.valueOf(id.toString()))
                .collect(Collectors.toSet());
        } catch (HttpClientErrorException.Forbidden e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces refuse par auth-service", e);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT invalide pour auth-service", e);
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erreur auth-service: " + e.getStatusCode(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Auth-service indisponible", e);
        }
    }

    private void validateVolunteerExists(Long volunteerId, Set<Long> volunteerIds) {
        if (volunteerId == null || !volunteerIds.contains(volunteerId)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Volontaire avec ID " + volunteerId + " non trouve dans auth-service"
            );
        }
    }

    private VolunteerProgramTaskDTO toDto(VolunteerProgramTask task) {
        return new VolunteerProgramTaskDTO(
            task.getVolunteerId(),
            task.getVolunteerName(),
            task.getTaskDate(),
            task.getStartTime(),
            task.getEndTime(),
            task.getLocation(),
            task.getRole()
        );
    }

    private VolunteerProgramTask toEntity(VolunteerProgramTaskDTO dto) {
        return new VolunteerProgramTask(
            dto.getVolunteerId(),
            dto.getVolunteerName(),
            dto.getTaskDate(),
            dto.getStartTime(),
            dto.getEndTime(),
            dto.getLocation(),
            dto.getRole()
        );
    }
}
