package com.example.booknestapp.entitytests;

import com.example.booknestapp.entity.User;
import com.example.booknestapp.model.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    @Test
    void testResolveUsername_NormalCase() {
        User user = new User(1L, "John", "Doe", "john.doe@example.com", "password123", Role.USER);
        assertEquals("jdoe", user.getUsername(), "Failed: Expected 'jdoe' for 'John Doe'.");
    }

    @Test
    void testResolveUsername_WithAccentedCharacters() {
        User user = new User(5L, "Émilie", "Durand", "emilie.durand@example.com", "password123", Role.USER);
        assertEquals("edurand", user.getUsername(), "Failed: Expected 'edurand' for 'Émilie Durand'.");
    }
}
