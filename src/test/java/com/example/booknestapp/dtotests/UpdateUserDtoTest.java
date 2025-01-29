package com.example.booknestapp.dtotests;

import com.example.booknestapp.dto.UpdateUserDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUpdateUserDto() {
        // Create a valid UpdateUserDto instance
        UpdateUserDto updateUserDto = new UpdateUserDto(1L, "John", "Doe", "john.doe@example.com", "password123");

        // Validate the object
        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(updateUserDto);

        // Ensure no validation errors
        assertTrue(violations.isEmpty(), "There should be no validation errors for a valid DTO");
        assertEquals(1L, updateUserDto.getId(), "ID should match");
        assertEquals("John", updateUserDto.getFirstName(), "First name should match");
        assertEquals("Doe", updateUserDto.getLastName(), "Last name should match");
        assertEquals("john.doe@example.com", updateUserDto.getEmail(), "Email should match");
        assertEquals("password123", updateUserDto.getPassword(), "Password should match");
    }

    @Test
    void testInvalidUpdateUserDto_EmptyFields() {
        // Create an UpdateUserDto with invalid fields
        UpdateUserDto updateUserDto = new UpdateUserDto(null, "", "", "", "password123");

        // Validate the object
        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(updateUserDto);

        // Ensure there are validation errors
        assertFalse(violations.isEmpty(), "Validation errors should be present for invalid DTO");

        // Check specific error messages
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("must not be empty")),
                "First name and last name should not be empty");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email should not be empty")),
                "Email should not be empty");
    }

    @Test
    void testInvalidUpdateUserDto_InvalidEmail() {
        // Create an UpdateUserDto with an invalid email
        UpdateUserDto updateUserDto = new UpdateUserDto(1L, "John", "Doe", "invalid-email", "password123");

        // Validate the object
        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(updateUserDto);

        // Ensure there are validation errors
        assertFalse(violations.isEmpty(), "Validation errors should be present for invalid email");

        // Check specific error messages
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be a well-formed email address")),
                "Email should be a valid email address");
    }

    @Test
    void testOptionalPassword() {
        // Create an UpdateUserDto without a password
        UpdateUserDto updateUserDto = new UpdateUserDto(1L, "John", "Doe", "john.doe@example.com", null);

        // Validate the object
        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(updateUserDto);

        // Ensure no validation errors
        assertTrue(violations.isEmpty(), "There should be no validation errors when the password is null");
    }
}
