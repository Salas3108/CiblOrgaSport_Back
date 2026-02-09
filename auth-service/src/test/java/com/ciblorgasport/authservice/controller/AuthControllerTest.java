package com.ciblorgasport.authservice.controller;

import com.ciblorgasport.authservice.dto.*;
import com.ciblorgasport.authservice.entity.User;
import com.ciblorgasport.authservice.entity.Role;
import com.ciblorgasport.authservice.repository.UserRepository;
import com.ciblorgasport.authservice.security.JwtUtils;
import com.ciblorgasport.authservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private JwtUtils jwtUtils;
    
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService, userRepository);
        authController.jwtUtils = jwtUtils;
    }

    @Test
    void hello_ReturnsGreeting() {
        assertEquals("Hello from Auth Service!", authController.hello());
    }

    @Test
    void login_ValidRequest_ReturnsJwtResponse() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("john");
        loginRequest.setPassword("password");
        
        JwtResponse jwtResponse = new JwtResponse("token123", "john", "john@test.com", "USER");
        
        when(authService.authenticateUser(loginRequest)).thenReturn(jwtResponse);

        // Act
        JwtResponse result = authController.login(loginRequest);

        // Assert
        assertEquals("token123", result.getToken());
        assertEquals("john", result.getUsername());
    }

    @Test
    void register_ValidRequest_ReturnsSuccessMessage() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("jane");
        registerRequest.setEmail("jane@test.com");
        registerRequest.setPassword("password");
        
        when(authService.registerUser(registerRequest)).thenReturn("User registered successfully!");

        // Act
        String result = authController.register(registerRequest);

        // Assert
        assertEquals("User registered successfully!", result);
    }

    @Test
    void getUserByUsername_WhenExists_ReturnsUserInfo() {
        // Arrange
        String username = "john";
        User user = new User("john", "john@test.com", "password123");
        user.setRole(Role.USER);
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        Map<String, Object> result = authController.getUserByUsername(username);

        // Assert
        assertEquals("john", result.get("username"));
        assertEquals("password123", result.get("password"));
        assertEquals("ROLE_USER", result.get("role"));
    }

    @Test
    void getUserByUsername_WhenNotExists_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authController.getUserByUsername("unknown");
        });
    }

    @Test
    void me_ValidToken_ReturnsUserInfo() {
        // Arrange
        String token = "valid.token.here";
        String authorization = "Bearer " + token;
        String username = "john";
        
        User user = new User("john", "john@test.com", "password");
        user.setRole(Role.USER);
        user.setValidated(true);
        
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<Map<String, Object>> response = authController.me(authorization);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("john", body.get("username"));
        assertEquals(true, body.get("validated"));
    }

    @Test
    void me_NoBearerToken_ReturnsUnauthorized() {
        // Arrange
        String authorization = "Invalid token format";

        // Act
        ResponseEntity<Map<String, Object>> response = authController.me(authorization);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void me_InvalidToken_ReturnsUnauthorized() {
        // Arrange
        String token = "invalid.token";
        String authorization = "Bearer " + token;
        
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.me(authorization);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void uploadDocuments_AthleteUser_UploadsDocuments() {
        // Arrange
        DocumentUploadRequest request = new DocumentUploadRequest();
        request.setUsername("athlete");
        request.setDocuments(Arrays.asList("doc1.pdf", "doc2.pdf"));
        
        User user = new User("athlete", "athlete@test.com", "password");
        user.setRole(Role.ATHLETE);
        
        when(userRepository.findByUsername("athlete")).thenReturn(Optional.of(user));

        // Act
        String result = authController.uploadDocuments(request);

        // Assert
        assertEquals("Documents envoyés pour validation.", result);
        verify(userRepository).save(user);
    }

    @Test
    void uploadDocuments_UserNotFound_ThrowsException() {
        // Arrange
        DocumentUploadRequest request = new DocumentUploadRequest();
        request.setUsername("unknown");
        
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authController.uploadDocuments(request);
        });
    }

    @Test
    void validateAthlete_ValidRequest_ValidatesAthlete() {
        // Arrange
        ValidateAthleteRequest request = new ValidateAthleteRequest();
        request.setUsername("athlete");
        request.setValidated(true);
        
        User user = new User("athlete", "athlete@test.com", "password");
        user.setRole(Role.ATHLETE);
        user.setValidated(false);
        
        when(userRepository.findByUsername("athlete")).thenReturn(Optional.of(user));

        // Act
        String result = authController.validateAthlete(request);

        // Assert
        assertEquals("Athlète validé.", result);
        verify(userRepository).save(user);
    }

    @Test
    void validateAthlete_RejectAthlete_ReturnsRejectedMessage() {
        // Arrange
        ValidateAthleteRequest request = new ValidateAthleteRequest();
        request.setUsername("athlete");
        request.setValidated(false);
        
        User user = new User("athlete", "athlete@test.com", "password");
        user.setRole(Role.ATHLETE);
        user.setValidated(true);
        
        when(userRepository.findByUsername("athlete")).thenReturn(Optional.of(user));

        // Act
        String result = authController.validateAthlete(request);

        // Assert
        assertEquals("Athlète rejeté.", result);
        verify(userRepository).save(user);
    }
}