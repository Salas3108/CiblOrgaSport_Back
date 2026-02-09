package com.ciblorgasport.authservice.service;

import com.ciblorgasport.authservice.entity.User;
import com.ciblorgasport.authservice.entity.Role;
import com.ciblorgasport.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;
    
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_WhenExists_ReturnsUserDetails() {
        // Arrange
        String username = "john";
        User user = new User(username, "john@test.com", "password");
        user.setRole(Role.USER);
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_WhenNotExists_ThrowsException() {
        // Arrange
        String username = "unknown";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
    }

    @Test
    void getUserByUsername_WhenExists_ReturnsUser() {
        // Arrange
        String username = "jane";
        User user = new User(username, "jane@test.com", "password");
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        User result = userDetailsService.getUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals("jane", result.getUsername());
    }

    @Test
    void save_CallsRepositorySave() {
        // Arrange
        User user = new User();

        // Act
        userDetailsService.save(user);

        // Assert
        verify(userRepository).save(user);
    }
}