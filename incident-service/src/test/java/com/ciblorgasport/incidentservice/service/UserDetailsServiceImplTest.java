package com.ciblorgasport.incidentservice.service;

import com.ciblorgasport.incidentservice.client.AuthServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private AuthServiceClient authServiceClient;
    
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl(authServiceClient);
    }

    @Test
    void loadUserByUsername_ValidUsername_ReturnsUserDetails() {
        // Arrange
        String username = "john";
        UserDetails expectedUser = mock(UserDetails.class);
        
        when(authServiceClient.fetchUserByUsername(username)).thenReturn(expectedUser);

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(username);

        // Assert
        assertSame(expectedUser, result);
        verify(authServiceClient, times(1)).fetchUserByUsername(username);
    }

    @Test
    void loadUserByUsername_NullUsername_ThrowsException() {
        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(null);
        });
        
        assertTrue(exception.getMessage().contains("User Not Found with username: null"));
        verify(authServiceClient, never()).fetchUserByUsername(any());
    }

    @Test
    void loadUserByUsername_EmptyUsername_ThrowsException() {
        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("");
        });
        
        assertTrue(exception.getMessage().contains("User Not Found with username: "));
        verify(authServiceClient, never()).fetchUserByUsername(any());
    }

    @Test
    void loadUserByUsername_ClientThrowsException_Propagates() {
        // Arrange
        String username = "john";
        UsernameNotFoundException expectedException = 
            new UsernameNotFoundException("User not found");
        
        when(authServiceClient.fetchUserByUsername(username))
            .thenThrow(expectedException);

        // Act & Assert
        UsernameNotFoundException actualException = assertThrows(
            UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername(username)
        );
        
        assertSame(expectedException, actualException);
        verify(authServiceClient, times(1)).fetchUserByUsername(username);
    }
}