package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.entity.User;
import com.example.booknestapp.model.Role;
import com.example.booknestapp.repository.UserRepository;
import com.example.booknestapp.scheduler.SchedulerService;
import com.example.booknestapp.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class SchedulerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    private SchedulerService schedulerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        schedulerService = new SchedulerService(userRepository, jwtService, userDetailsService);
    }

    @Test
    void runScheduledTask_shouldProcessMockUsers() {
        // Arrange
        List<User> mockUsers = List.of(
                new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER),
                new User(2L, "Jane", "Smith", "jane.smith@example.com", "password", Role.USER)
        );
        when(userRepository.findAll()).thenReturn(mockUsers);

        // Act
        schedulerService.runScheduledTask();

        // Assert
        verify(userRepository, times(1)).findAll();
        mockUsers.forEach(user -> System.out.println("Processed user: " + user.getEmail()));
    }

    @Test
    void runScheduledTask_shouldHandleEmptyRepository() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        schedulerService.runScheduledTask();

        // Assert
        verify(userRepository, times(1)).findAll();
        System.out.println("No users found to process.");
    }

    @Test
    void cleanExpiredTokens_shouldHandleValidationErrors() {
        // Arrange
        User mockUser = new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                mockUser.getEmail(),
                mockUser.getPassword(),
                mockUser.getRole().getAuthorities()
        );
        when(userRepository.findAll()).thenReturn(List.of(mockUser));
        when(userDetailsService.loadUserByUsername(mockUser.getEmail())).thenReturn(userDetails);
        when(jwtService.isTokenValid(eq("dummyToken"), eq(userDetails))).thenThrow(new RuntimeException("Validation error"));

        // Act
        schedulerService.cleanExpiredTokens();

        // Assert
        verify(userRepository, times(1)).findAll();
        verify(jwtService, times(1)).isTokenValid(eq("dummyToken"), eq(userDetails));
        System.out.println("Handled validation error for token cleanup.");
    }

    @Test
    void cleanExpiredTokens_shouldHandleValidTokens() {
        // Arrange
        User mockUser = new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                mockUser.getEmail(),
                mockUser.getPassword(),
                mockUser.getRole().getAuthorities()
        );
        when(userRepository.findAll()).thenReturn(List.of(mockUser));
        when(userDetailsService.loadUserByUsername(mockUser.getEmail())).thenReturn(userDetails);
        when(jwtService.isTokenValid(eq("dummyToken"), eq(userDetails))).thenReturn(true);

        // Act
        schedulerService.cleanExpiredTokens();

        // Assert
        verify(userRepository, times(1)).findAll();
        verify(jwtService, times(1)).isTokenValid(eq("dummyToken"), eq(userDetails));
        System.out.println("Valid token found for user: " + mockUser.getEmail());
    }

    @Test
    void cleanExpiredTokens_shouldHandleInvalidTokens() {
        // Arrange
        User mockUser = new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                mockUser.getEmail(),
                mockUser.getPassword(),
                mockUser.getRole().getAuthorities()
        );
        when(userRepository.findAll()).thenReturn(List.of(mockUser));
        when(userDetailsService.loadUserByUsername(mockUser.getEmail())).thenReturn(userDetails);
        when(jwtService.isTokenValid(eq("dummyToken"), eq(userDetails))).thenReturn(false);

        // Act
        schedulerService.cleanExpiredTokens();

        // Assert
        verify(userRepository, times(1)).findAll();
        verify(jwtService, times(1)).isTokenValid(eq("dummyToken"), eq(userDetails));
        System.out.println("Expired tokens cleaned up for: " + mockUser.getEmail());
    }
}
