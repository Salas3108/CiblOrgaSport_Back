package com.ciblorgasport.authservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.DeleteMapping;

import com.ciblorgasport.authservice.dto.DocumentUploadRequest;
import com.ciblorgasport.authservice.dto.InternalAthleteSummary;
import com.ciblorgasport.authservice.dto.JwtResponse;
import com.ciblorgasport.authservice.dto.LoginRequest;
import com.ciblorgasport.authservice.dto.RegisterRequest;
import com.ciblorgasport.authservice.dto.ValidateAthleteRequest;
import com.ciblorgasport.authservice.entity.User;
import com.ciblorgasport.authservice.entity.Role;
import com.ciblorgasport.authservice.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import com.ciblorgasport.authservice.security.JwtUtils;
import com.ciblorgasport.authservice.service.AuthService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
        // ATHLETE : upload documents
        @PostMapping("/user/upload-documents")
        @PreAuthorize("hasRole('ATHLETE')")
        public String uploadDocuments(@RequestBody DocumentUploadRequest request) {
            User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
            user.setDocuments(request.getDocuments());
            userRepository.save(user);
            return "Documents envoyés pour validation.";
        }

        // ADMIN : valider ou rejeter un athlète
        @PostMapping("/admin/validate-athlete")
        @PreAuthorize("hasRole('ADMIN')")
        public String validateAthlete(@RequestBody ValidateAthleteRequest request) {
            User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
            user.setValidated(request.isValidated());
            userRepository.save(user);
            return request.isValidated() ? "Athlète validé." : "Athlète rejeté.";
        }

        // ADMIN : valider ou rejeter un volontaire
        @PostMapping("/admin/validate-volunteer")
        @PreAuthorize("hasRole('ADMIN')")
        public String validateVolunteer(@RequestBody ValidateAthleteRequest request) {
            User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
            user.setValidated(request.isValidated());
            userRepository.save(user);
            return request.isValidated() ? "Volontaire validé." : "Volontaire rejeté.";
        }

        // ADMIN : récupérer la liste des athlètes (validés ou non)
        @GetMapping("/admin/athletes")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> getAthletes(@RequestParam(required = false) Boolean validated) {
            org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authorities: " + authentication.getAuthorities());
            if (validated == null) {
                // Tous les athlètes
                return ResponseEntity.ok(userRepository.findByRole(com.ciblorgasport.authservice.entity.Role.ATHLETE));
            } else {
                // Filtrer par statut de validation
                return ResponseEntity.ok(userRepository.findByRoleAndValidated(com.ciblorgasport.authservice.entity.Role.ATHLETE, validated));
            }
        }

        // ADMIN : récupérer la liste des volontaires (validés ou non)
        @GetMapping("/admin/volunteers")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> getVolunteers(@RequestParam(required = false) Boolean validated) {
            if (validated == null) {
                return ResponseEntity.ok(userRepository.findByRole(com.ciblorgasport.authservice.entity.Role.VOLONTAIRE));
            } else {
                return ResponseEntity.ok(userRepository.findByRoleAndValidated(com.ciblorgasport.authservice.entity.Role.VOLONTAIRE, validated));
            }
        }

        // INTERNAL : list athletes (id + username) for participants sync
        @GetMapping("/internal/athletes")
        public ResponseEntity<List<InternalAthleteSummary>> listAthletesInternal() {
            List<InternalAthleteSummary> athletes = userRepository.findByRole(Role.ATHLETE).stream()
                    .map(user -> new InternalAthleteSummary(user.getId(), user.getUsername()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(athletes);
        }

    private final AuthService authService;
    private final UserRepository userRepository;

    @Autowired JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }
    @GetMapping("/user/username/{username}")
    public Map<String, Object> getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        map.put("role", "ROLE_" + user.getRole().name());
        map.put("email", user.getEmail());
        return map;
    }

    // Retrieve user by numeric id
    @GetMapping("/user/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getId());
                    map.put("username", user.getUsername());
                    map.put("email", user.getEmail());
                    map.put("role", "ROLE_" + user.getRole().name());
                    map.put("validated", user.isValidated());
                    return ResponseEntity.ok(map);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Check existence by id (used by other microservices)
    @GetMapping("/user/exists/{id}")
    public ResponseEntity<Boolean> userExists(@PathVariable Long id) {
        boolean exists = userRepository.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorization.substring(7);
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("email", user.getEmail());
        map.put("role", "ROLE_" + user.getRole().name());
        map.put("validated", user.isValidated());
        map.put("documents", user.getDocuments());
        map.put("createdAt", user.getCreatedAt());
        map.put("updatedAt", user.getUpdatedAt());
        return ResponseEntity.ok(map);
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }

    // RGPD : Droit à l'oubli - suppression du compte utilisateur connecté
    @DeleteMapping("/delete-account")
    @Transactional
    public ResponseEntity<?> deleteMyAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token manquant ou invalide");
        }
        String token = authorization.substring(7);
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalide");
        }
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }
        System.out.println("[deleteMyAccount] username=" + user.getUsername() + ", role=" + user.getRole() + ", validated=" + user.isValidated() + ", id=" + user.getId());
        userRepository.delete(user);
        userRepository.flush();
        System.out.println("[deleteMyAccount] Suppression demandée pour id=" + user.getId() + ", username=" + user.getUsername());
        return ResponseEntity.ok("Compte supprimé avec succès");
    }
}
