package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.security.filter.JwtTokenAuthenticationFilter;
import com.example.booknestapp.security.service.AuthenticationService;
import com.example.booknestapp.security.service.CustomUserDetailsService;
import com.example.booknestapp.security.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class JwtTokenAuthenticationFilterTest {

    private JwtTokenAuthenticationFilter filter;
    private JwtService jwtService;
    private CustomUserDetailsService customUserDetailsService;
    private AuthenticationService authenticationService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        customUserDetailsService = mock(CustomUserDetailsService.class);
        authenticationService = mock(AuthenticationService.class);
        filter = new JwtTokenAuthenticationFilter(jwtService, customUserDetailsService, authenticationService, customUserDetailsService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    void testFilter_PermitAllUri() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/public/resource");
        when(authenticationService.checkIsPermitAll("public")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void testFilter_LoginUri() throws Exception {
        // Arrange: Mock the request URI to be "/login"
        when(request.getRequestURI()).thenReturn("/login");

        // Act: Call the filter
        filter.doFilter(request, response, filterChain);

        // Assert: Ensure the filter chain proceeds for "/login"
        verify(filterChain, times(1)).doFilter(request, response);

        // Assert: Ensure no redirects or authentication happen for "/login"
        verify(response, never()).sendRedirect(anyString());
    }



    @Test
    void testFilter_HomeRedirectWithoutJwt() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/");
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendRedirect("/login");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testFilter_MissingJwtCookie() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/protected/resource");
        when(request.getCookies()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendRedirect("/login");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testFilter_AuthenticateWithRoles() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/protected/resource");
        Cookie jwtCookie = new Cookie("jwt", "valid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtService.extractEmail("valid-token")).thenReturn("user@example.com");

        UserDetails userDetails = mock(UserDetails.class);
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(jwtService.extractAllClaims("valid-token")).thenReturn(claims);
        when(claims.get("roles")).thenReturn(List.of("ROLE_USER"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testShouldNotFilter_PublicPath() {
        when(request.getRequestURI()).thenReturn("/public/resource");

        try {
            assertTrue(filter.shouldNotFilter(request));
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testShouldNotFilter_LoginPath() {
        when(request.getRequestURI()).thenReturn("/login");

        try {
            assertTrue(filter.shouldNotFilter(request));
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testShouldNotFilter_UnhandledPath() {
        // Arrange: Simulate an unhandled path
        when(request.getRequestURI()).thenReturn("/some/unhandled/path");

        // Act: Call shouldNotFilter
        boolean result = false;
        try {
            result = filter.shouldNotFilter(request);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        // Assert: Ensure the path is not skipped
        assertFalse(result, "Unhandled path should not bypass filtering");
    }

    @Test
    void testFilter_NoCookies() throws ServletException, IOException {
        // Arrange: Simulate no cookies in the request
        when(request.getRequestURI()).thenReturn("/protected/resource");
        when(request.getCookies()).thenReturn(null);

        // Act: Execute the filter
        filter.doFilterInternal(request, response, filterChain);

        // Assert: Ensure redirection to "/login"
        verify(response, times(1)).sendRedirect("/login");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testFilter_InvalidEmailInJwt() throws ServletException, IOException {
        // Arrange: Simulate a JWT with an invalid email
        when(request.getRequestURI()).thenReturn("/protected/resource");
        Cookie jwtCookie = new Cookie("jwt", "valid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtService.extractEmail("valid-token")).thenReturn(null); // Invalid email

        // Act: Execute the filter
        filter.doFilterInternal(request, response, filterChain);

        // Assert: Ensure redirection to "/login"
        verify(response, times(1)).sendRedirect("/login");
        verify(filterChain, never()).doFilter(request, response);
    }



    @Test
    void testShouldNotFilter_RandomPublicPath() throws ServletException {
        // Arrange: Simulate a public path
        when(request.getRequestURI()).thenReturn("/public/random");

        // Act: Call shouldNotFilter
        boolean result = filter.shouldNotFilter(request);

        // Assert: Ensure public paths bypass filtering
        assertTrue(result);
    }

    @Test
    void testFilter_InvalidTokenThrowsException() throws ServletException, IOException {
        // Arrange: Simulate a protected path with an invalid token causing an exception
        when(request.getRequestURI()).thenReturn("/protected/resource");
        Cookie jwtCookie = new Cookie("jwt", "invalid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtService.extractEmail("invalid-token")).thenThrow(new RuntimeException("Invalid token"));

        // Act: Execute the filter
        filter.doFilterInternal(request, response, filterChain);

        // Assert: Ensure redirection to "/login"
        verify(response, times(1)).sendRedirect("/login");
        verify(filterChain, never()).doFilter(request, response);
    }


    @Test
    void testFilter_HomeRedirectWithValidJwt() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/");
        Cookie jwtCookie = new Cookie("jwt", "valid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtService.extractEmail("valid-token")).thenReturn("user@example.com");

        UserDetails userDetails = mock(UserDetails.class);
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(jwtService.extractAllClaims("valid-token")).thenReturn(claims);
        when(claims.get("roles")).thenReturn(List.of("ROLE_USER"));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).sendRedirect("/home");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testFilter_AuthenticateWithoutRoles() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/protected/resource");
        Cookie jwtCookie = new Cookie("jwt", "valid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtService.extractEmail("valid-token")).thenReturn("user@example.com");

        UserDetails userDetails = mock(UserDetails.class);
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(jwtService.extractAllClaims("valid-token")).thenReturn(claims);
        when(claims.get("roles")).thenReturn(null); // Simulate no roles

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }



    @Test
    void testFilter_UnknownPathProceedsWithoutRedirect() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/unknown/path");
        Cookie jwtCookie = new Cookie("jwt", "valid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void testFilter_UnknownPathProceeds() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/unknown");
        Cookie jwtCookie = new Cookie("jwt", "valid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void testFilter_NoJwtCookie() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/protected/resource");
        when(request.getCookies()).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, times(1)).sendRedirect("/login");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void testFilter_InvalidJwtToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/protected/resource");
        Cookie jwtCookie = new Cookie("jwt", "invalid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtService.extractEmail("invalid-token")).thenThrow(new RuntimeException("Invalid JWT Token"));

        filter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendRedirect("/login");
        verify(filterChain, never()).doFilter(request, response);
    }


    @Test
    void testFilter_ValidJwtTokenWithoutEmail() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/protected/resource");
        Cookie jwtCookie = new Cookie("jwt", "valid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtService.extractEmail("valid-token")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendRedirect("/login");
        verify(filterChain, never()).doFilter(request, response);
    }
    @Test
    void testFilter_NoRolesInClaims() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/protected/resource");
        Cookie jwtCookie = new Cookie("jwt", "valid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtService.extractEmail("valid-token")).thenReturn("user@example.com");

        UserDetails userDetails = mock(UserDetails.class);
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

        // Mock JWT claims with null roles
        Claims claims = mock(Claims.class);
        when(jwtService.extractAllClaims("valid-token")).thenReturn(claims);
        when(claims.get("roles")).thenReturn(null); // Simulate null roles

        // Act
        try {
            filter.doFilterInternal(request, response, filterChain);
        } catch (Exception e) {
            fail("Filter threw an unexpected exception: " + e.getMessage());
        }

        // Assert
        verify(response, times(1)).sendRedirect("/login"); // Expect redirection to login
        verify(filterChain, never()).doFilter(request, response); // Ensure filter chain is not invoked
    }



    @Test
    void testShouldNotFilter_RootPath() throws ServletException {
        when(request.getRequestURI()).thenReturn("/");

        boolean result = filter.shouldNotFilter(request);

        assertFalse(result); // Root path should not bypass filtering
    }

}
