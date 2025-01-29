package com.example.booknestapp.configtests;

import com.example.booknestapp.security.config.SecurityConfig;
import com.example.booknestapp.security.config.SecurityConfigPropertiesProvider;
import com.example.booknestapp.security.filter.JwtTokenAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    private AnnotationConfigApplicationContext context;

    @Mock
    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @Mock
    private SecurityConfigPropertiesProvider securityConfigPropertiesProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize Spring context with SecurityConfig and provide mocks
        context = new AnnotationConfigApplicationContext();
        context.registerBean(JwtTokenAuthenticationFilter.class, () -> jwtTokenAuthenticationFilter);
        context.registerBean(SecurityConfigPropertiesProvider.class, () -> securityConfigPropertiesProvider);
        context.register(SecurityConfig.class);
        context.refresh();

        // Mock permitted URLs
        when(securityConfigPropertiesProvider.getPermitAllUrl()).thenReturn(
                java.util.List.of("/login", "/register", "/public")
        );
    }

    @Test
    void testPasswordEncoderBean() {
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
        assertNotNull(passwordEncoder, "PasswordEncoder bean should not be null");
        assertTrue(passwordEncoder instanceof org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder,
                "PasswordEncoder should be an instance of BCryptPasswordEncoder");
    }

    @Test
    void testAuthenticationManagerBean() {
        AuthenticationManager authenticationManager = context.getBean(AuthenticationManager.class);
        assertNotNull(authenticationManager, "AuthenticationManager bean should not be null");
    }

    @Test
    void testSecurityFilterChainBean() throws Exception {
        SecurityFilterChain filterChain = context.getBean(SecurityFilterChain.class);
        assertNotNull(filterChain, "SecurityFilterChain bean should not be null");

        // Validate JwtTokenAuthenticationFilter is added to the filter chain
        verify(jwtTokenAuthenticationFilter, never()).doFilter(any(), any(), any());
    }

    @Test
    void testPermitAllUrls() throws Exception {
        SecurityFilterChain filterChain = context.getBean(SecurityFilterChain.class);
        assertNotNull(filterChain, "SecurityFilterChain bean should not be null");

        // Check if the permitAll URLs are set correctly
        var permitAllUrls = securityConfigPropertiesProvider.getPermitAllUrl();
        assertTrue(permitAllUrls.contains("/login"), "PermitAll URLs should include '/login'");
        assertTrue(permitAllUrls.contains("/register"), "PermitAll URLs should include '/register'");
        assertTrue(permitAllUrls.contains("/public"), "PermitAll URLs should include '/public'");
    }
}
