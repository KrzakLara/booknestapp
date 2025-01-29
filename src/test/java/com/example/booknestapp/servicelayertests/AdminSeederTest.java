package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.entity.User;
import com.example.booknestapp.model.Role;
import com.example.booknestapp.repository.UserRepository;
import com.example.booknestapp.seeder.AdminSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminSeederTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminSeeder adminSeeder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminSeeder = new AdminSeeder(userRepository, passwordEncoder);
    }

    @Test
    void testRun_WhenAdminDoesNotExist() {
        // Mock behavior: Admin does not exist
        when(userRepository.findByEmail("admin@example.com")).thenReturn(null);
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");

        // Run the seeder
        adminSeeder.run();

        // Capture the saved user
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedAdmin = userCaptor.getValue();

        // Validate the saved admin details
        assertNotNull(savedAdmin, "Saved admin should not be null");
        assertEquals("Admin", savedAdmin.getFirstName(), "Admin first name should match");
        assertEquals("User", savedAdmin.getLastName(), "Admin last name should match");
        assertEquals("auser", savedAdmin.getUsername(), "Admin username should match");
        assertEquals("admin@example.com", savedAdmin.getEmail(), "Admin email should match");
        assertEquals("encodedPassword", savedAdmin.getPassword(), "Admin password should be encoded");
        assertEquals(Role.ADMIN, savedAdmin.getRole(), "Admin role should be ADMIN");
    }

    @Test
    void testRun_WhenAdminAlreadyExists() {
        // Mock behavior: Admin already exists
        User existingAdmin = new User();
        existingAdmin.setEmail("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(existingAdmin);

        // Run the seeder
        adminSeeder.run();

        // Verify no new admin is saved
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}
