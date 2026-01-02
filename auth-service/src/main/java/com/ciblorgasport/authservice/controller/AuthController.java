package com.ciblorgasport.authservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.ciblorgasport.authservice.dto.DocumentUploadRequest;
import com.ciblorgasport.authservice.dto.ValidateAthleteRequest;

import com.ciblorgasport.authservice.dto.JwtResponse;
import com.ciblorgasport.authservice.dto.LoginRequest;
import com.ciblorgasport.authservice.dto.RegisterRequest;
import com.ciblorgasport.authservice.entity.User;
import com.ciblorgasport.authservice.repository.UserRepository;
import com.ciblorgasport.authservice.service.AuthService;

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
    private final AuthService authService;
    private final UserRepository userRepository;

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
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        map.put("role", "ROLE_" + user.getRole().name());
        map.put("email", user.getEmail());
        return map;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Auth Service!";
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }
}
