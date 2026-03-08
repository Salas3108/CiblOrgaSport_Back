package com.ciblorgasport.authservice.service;

import com.ciblorgasport.authservice.dto.LoginRequest;
import com.ciblorgasport.authservice.dto.RegisterRequest;
import com.ciblorgasport.authservice.dto.JwtResponse;
import com.ciblorgasport.authservice.entity.User;
import com.ciblorgasport.authservice.entity.Role;
import com.ciblorgasport.authservice.repository.UserRepository;
import com.ciblorgasport.authservice.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RestTemplate restTemplate;
    private final String participantsBaseUrl;
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                       @Value("${participants-service.base-url:http://localhost:8087}") String participantsBaseUrl) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.participantsBaseUrl = participantsBaseUrl;
        this.restTemplate = new RestTemplate();
    }
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        if ((user.getRole() == Role.COMMISSAIRE || user.getRole() == Role.VOLONTAIRE) && !user.isValidated()) {
            throw new RuntimeException("Votre compte doit être validé par un administrateur.");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        return new JwtResponse(jwt, user.getUsername(), user.getEmail(), user.getRole().name());
    }
    public String registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return "Error: Username is already taken!";
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return "Error: Email is already in use!";
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        if (registerRequest.getRole() != null) {
            user.setRole(registerRequest.getRole());
        } else {
            user.setRole(Role.USER);
        }
        // ATHLETE et VOLONTAIRE nécessitent validation par admin
        if (user.getRole() == Role.ATHLETE || user.getRole() == Role.VOLONTAIRE) {
            user.setValidated(false);
        } else {
            user.setValidated(true);
        }
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setUpdatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
        if (user.getRole() == Role.ATHLETE) {
            syncAthleteProfile(user.getId(), user.getUsername());
        }
        return "User registered successfully!";
    }

    private void syncAthleteProfile(Long userId, String username) {
        try {
            if (userId == null || username == null || username.isBlank()) return;
            String url = participantsBaseUrl + "/internal/athletes";
            Map<String, Object> payload = Map.of("id", userId, "username", username);
            restTemplate.postForEntity(url, payload, Object.class);
        } catch (Exception ex) {
            logger.warn("Failed to sync athlete profile to participants-service", ex);
        }
    }
}
