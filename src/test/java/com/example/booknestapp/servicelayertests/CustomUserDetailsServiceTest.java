package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.entity.User;
import com.example.booknestapp.model.Role;
import com.example.booknestapp.repository.UserRepository;
import com.example.booknestapp.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        // Mock user data
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password123");
        mockUser.setRole(Role.USER);

        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);

        // Execute the method
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // Validate the result
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals("test@example.com", userDetails.getUsername(), "Email should match the username");
        assertEquals("password123", userDetails.getPassword(), "Password should match");
        assertEquals(mockUser.getRole().getAuthorities().size(), userDetails.getAuthorities().size(), "Authorities should match");

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // Execute and validate the exception
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("nonexistent@example.com"),
                "Should throw UsernameNotFoundException for nonexistent user"
        );

        assertEquals("Invalid username or password.", exception.getMessage(), "Exception message should match");

        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }
}
