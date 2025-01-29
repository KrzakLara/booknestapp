package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.security.config.SecurityConfigPropertiesProvider;
import com.example.booknestapp.security.service.IAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IAuthenticationServiceTest {

    @Mock
    private SecurityConfigPropertiesProvider securityConfigPropertiesProvider;

    private IAuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new IAuthenticationService(securityConfigPropertiesProvider);
    }

    @Test
    void testGetAuthenticatedUserEmail() {
        // Mock SecurityContext and Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        // Set up the mocked SecurityContext
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");

        // Call the method and verify
        String email = authenticationService.getAuthenticatedUserEmail();
        assertEquals("test@example.com", email);
    }

    @Test
    void testCheckIsPermitAll() {
        // Mock the SecurityConfigPropertiesProvider
        when(securityConfigPropertiesProvider.getPermitAllUrl()).thenReturn(List.of("/login", "/register"));

        // Call the method and verify
        boolean result = authenticationService.checkIsPermitAll("/login");
        assertTrue(result);

        boolean resultNotAllowed = authenticationService.checkIsPermitAll("/dashboard");
        assertFalse(resultNotAllowed);
    }
}
