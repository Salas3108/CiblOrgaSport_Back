package com.ciblorgasport.authservice.service;

import com.ciblorgasport.authservice.dto.JwtResponse;
import com.ciblorgasport.authservice.dto.LoginRequest;
import com.ciblorgasport.authservice.dto.RegisterRequest;
import com.ciblorgasport.authservice.entity.User;
import com.ciblorgasport.authservice.entity.Role;
import com.ciblorgasport.authservice.repository.UserRepository;
import com.ciblorgasport.authservice.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtils jwtUtils;
    
    @Mock
    private Authentication authentication;
    
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authenticationManager, userRepository, passwordEncoder, jwtUtils, "http://localhost:8087");
    }

    @Test
    void authenticateUser_ValidCredentials_ReturnsJwtResponse() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("john");
        loginRequest.setPassword("password");
        
        User user = new User("john", "john@test.com", "password");
        user.setRole(Role.USER);
        user.setValidated(true);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt.token.here");

        // Act
        JwtResponse result = authService.authenticateUser(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("jwt.token.here", result.getToken());
        assertEquals("john", result.getUsername());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void registerUser_NewUser_ReturnsSuccess() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@test.com");
        registerRequest.setPassword("password");
        registerRequest.setRole(Role.USER);
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // CORRECTION : Simplement retourner l'utilisateur sans set l'ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> 
            invocation.getArgument(0)  // Retourne l'utilisateur tel quel
        );

        // Act
        String result = authService.registerUser(registerRequest);

        // Assert
        assertEquals("User registered successfully!", result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password");
    }

    @Test
    void registerUser_UsernameTaken_ReturnsError() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("taken");
        registerRequest.setEmail("new@test.com");
        
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        // Act
        String result = authService.registerUser(registerRequest);

        // Assert
        assertEquals("Error: Username is already taken!", result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_AthleteRole_SetsNotValidated() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("athlete");
        registerRequest.setEmail("athlete@test.com");
        registerRequest.setPassword("password");
        registerRequest.setRole(Role.ATHLETE);
        
        when(userRepository.existsByUsername("athlete")).thenReturn(false);
        when(userRepository.existsByEmail("athlete@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        
        // CORRECTION : Utiliser argThat pour vérifier sans setter d'ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> 
            invocation.getArgument(0)
        );

        // Act
        authService.registerUser(registerRequest);

        // Assert - Vérifie les propriétés sans toucher à l'ID
        verify(userRepository).save(argThat(user -> 
            user.getRole() == Role.ATHLETE && !user.isValidated()
        ));
    }

    @Test
    void registerUser_UserRole_SetsValidated() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@test.com");
        registerRequest.setPassword("password");
        registerRequest.setRole(Role.USER);
        
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> 
            invocation.getArgument(0)
        );

        // Act
        authService.registerUser(registerRequest);

        // Assert
        verify(userRepository).save(argThat(user -> 
            user.getRole() == Role.USER && user.isValidated()
        ));
    }
}