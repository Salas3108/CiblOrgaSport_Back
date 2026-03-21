package com.ciblorgasport.participantsservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.ciblorgasport.participantsservice.dto.AthleteMapper;
import com.ciblorgasport.participantsservice.dto.EquipeDto;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteDocsRequest;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteInfoRequest;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteObservationRequest;
import com.ciblorgasport.participantsservice.service.AthleteService;
import com.ciblorgasport.participantsservice.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;

/**
 * Endpoints côté ATHLETE.
 *
 * NOTE: on utilise des POST comme demandé.
 */
@RestController
@RequestMapping({"/athlete", "/api/athlete"})
@PreAuthorize("hasRole('ATHLETE')")
public class AthleteController {

    private final AthleteService athleteService;
    private final AthleteMapper athleteMapper;
    private final JwtUtils jwtUtils;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${auth-service.base-url:http://localhost:8081}")
    private String authServiceBaseUrl;

    public AthleteController(AthleteService athleteService, AthleteMapper athleteMapper, JwtUtils jwtUtils) {
        this.athleteService = athleteService;
        this.athleteMapper = athleteMapper;
        this.jwtUtils = jwtUtils;
    }

    // ATHLETE : post info
    @PostMapping("/{id}/info")
    public ResponseEntity<?> postInfo(@PathVariable Long id, @RequestBody UpdateAthleteInfoRequest request, HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null || !tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden: token user id does not match path id"));
        }
        return ResponseEntity.ok(athleteMapper.toDto(athleteService.updateInfo(id, request)));
    }

    // ATHLETE : post doc
    @PostMapping("/{id}/doc")
    public ResponseEntity<?> postDoc(@PathVariable Long id, @RequestBody UpdateAthleteDocsRequest request, HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null || !tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden: token user id does not match path id"));
        }
        return ResponseEntity.ok(athleteMapper.toDto(athleteService.updateDocs(id, request)));
    }

    // ATHLETE : upload doc PDF (multipart)
    @PostMapping(value = "/{id}/doc/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocPdf(@PathVariable Long id,
                                          @org.springframework.web.bind.annotation.RequestPart(value = "certificatMedical", required = false) MultipartFile certificatMedical,
                                          @org.springframework.web.bind.annotation.RequestPart(value = "passport", required = false) MultipartFile passport,
                                          HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null || !tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden: token user id does not match path id"));
        }

        byte[] certificatBytes = extractPdfBytes(certificatMedical, "certificatMedical");
        byte[] passportBytes = extractPdfBytes(passport, "passport");

        return ResponseEntity.ok(athleteMapper.toDtoWithDownloadUrls(
                athleteService.updateDocsFiles(id, certificatBytes, passportBytes),
                String.valueOf(id)
        ));
    }

    // ATHLETE : download PDF document (allowed for ATHLETE owner or COMMISSAIRE/ADMIN)
    @GetMapping(value = "/{id}/doc/{docType}")
    @PreAuthorize("hasAnyRole('ATHLETE', 'COMMISSAIRE', 'ADMIN')")
    public ResponseEntity<?> downloadDoc(@PathVariable Long id,
                                        @PathVariable String docType,
                                        HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        String userRole = extractUserRoleFromRequest(httpRequest);
        
        // Allow if: user is the athlete OR user is COMMISSAIRE/ADMIN
        boolean isAthleteOwner = tokenUserId != null && tokenUserId.equals(id);
        boolean isCommissaireOrAdmin = userRole != null && 
                (userRole.contains("COMMISSAIRE") || userRole.contains("ADMIN"));
        
        if (!isAthleteOwner && !isCommissaireOrAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden: not authorized to download this document"));
        }

        try {
            byte[] pdfBytes = athleteService.getPdfBytes(id, docType);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(docType + ".pdf")
                    .build());
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ATHLETE : post remarque (observation)
    @PostMapping("/{id}/remarque")
    public ResponseEntity<?> postRemarque(@PathVariable Long id, @RequestBody UpdateAthleteObservationRequest request, HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null || !tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden: token user id does not match path id"));
        }
        return ResponseEntity.ok(athleteMapper.toDto(athleteService.updateObservation(id, request)));
    }

    // ATHLETE : get equipe detail
    @GetMapping("/{id}/equipe")
    public ResponseEntity<?> getEquipe(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long tokenUserId = extractUserIdFromRequest(httpRequest);
        if (tokenUserId == null || !tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden: token user id does not match path id"));
        }

        // Récupère l'équipe avec le nouveau DTO
        EquipeDto equipe = athleteService.getEquipeForAthlete(id);
        if (equipe == null) {
            return ResponseEntity.ok(Map.of(
                    "id", null,
                    "nom", null,
                    "pays", null,
                    "athleteIdUsernameMap", Map.of()
            ));
        }

        // Vérifie et complète les usernames manquants depuis auth-service
        Map<Long, String> updatedMap = equipe.getAthleteIdUsernameMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String username = entry.getValue();
                            if (username == null || username.isBlank()) {
                                username = fetchUsernameFromAuth(entry.getKey());
                                if (username != null && !username.isBlank()) {
                                    athleteService.updateUsernameIfMissing(entry.getKey(), username);
                                } else {
                                    username = "";
                                }
                            }
                            return username;
                        }
                ));
        equipe.setAthleteIdUsernameMap(updatedMap);

        return ResponseEntity.ok(equipe);
    }

    private String fetchUsernameFromAuth(Long athleteId) {
        if (athleteId == null) return null;
        try {
            String url = authServiceBaseUrl + "/auth/user/" + athleteId;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Object username = response != null ? response.get("username") : null;
            return username != null ? String.valueOf(username) : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7);
        return jwtUtils.getUserIdFromJwtToken(token);
    }

    private String extractUserRoleFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7);
        return jwtUtils.getRoleFromJwtToken(token);
    }

    private byte[] extractPdfBytes(MultipartFile file, String fieldName) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        boolean isPdf = "application/pdf".equalsIgnoreCase(contentType) || fileName.endsWith(".pdf");
        if (!isPdf) {
            throw new IllegalArgumentException(fieldName + " doit être un fichier PDF");
        }

        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("Impossible de lire le fichier " + fieldName);
        }
    }
}