package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.security.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAuthenticatedUserEmail() {
        // Arrange
        String expectedEmail = "user@example.com";
        when(authenticationService.getAuthenticatedUserEmail()).thenReturn(expectedEmail);

        // Act
        String actualEmail = authenticationService.getAuthenticatedUserEmail();

        // Assert
        assertEquals(expectedEmail, actualEmail);
        verify(authenticationService, times(1)).getAuthenticatedUserEmail();
    }

    @Test
    void testCheckIsPermitAll_WithPermitUri() {
        // Arrange
        String uriFirst = "/public";
        when(authenticationService.checkIsPermitAll(uriFirst)).thenReturn(true);

        // Act
        boolean result = authenticationService.checkIsPermitAll(uriFirst);

        // Assert
        assertTrue(result, "The URI should be permitted.");
        verify(authenticationService, times(1)).checkIsPermitAll(uriFirst);
    }

    @Test
    void testCheckIsPermitAll_WithNonPermitUri() {
        // Arrange
        String uriFirst = "/private";
        when(authenticationService.checkIsPermitAll(uriFirst)).thenReturn(false);

        // Act
        boolean result = authenticationService.checkIsPermitAll(uriFirst);

        // Assert
        assertFalse(result, "The URI should not be permitted.");
        verify(authenticationService, times(1)).checkIsPermitAll(uriFirst);
    }
}
