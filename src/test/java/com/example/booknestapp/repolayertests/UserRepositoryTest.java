package com.example.booknestapp.repolayertests;

import com.example.booknestapp.entity.User;
import com.example.booknestapp.model.Role;
import com.example.booknestapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Create a new User entity and set all required fields
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("jane.smith@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setUsername("jsmith"); // Explicitly set the username to avoid null constraint violation

        // Save the user to the repository
        userRepository.save(user);
    }

    @Test
    void testFindByEmail() {
        // Retrieve the user by email
        User user = userRepository.findByEmail("jane.smith@example.com");

        // Verify the retrieved user details
        assertNotNull(user);
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("jsmith", user.getUsername()); // Verify the username
    }

    @Test
    void testFindByEmailNotFound() {
        // Attempt to retrieve a non-existent user
        User user = userRepository.findByEmail("nonexistent@example.com");

        // Verify that no user is found
        assertNull(user);
    }
}
