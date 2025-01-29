package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.security.service.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private Claims claims;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExtractEmail() {
        // Arrange
        String token = "sample.jwt.token";
        String expectedEmail = "user@example.com";
        when(jwtService.extractEmail(token)).thenReturn(expectedEmail);

        // Act
        String actualEmail = jwtService.extractEmail(token);

        // Assert
        assertEquals(expectedEmail, actualEmail, "The extracted email should match the expected value.");
        verify(jwtService, times(1)).extractEmail(token);
    }

    @Test
    void testExtractClaim() {
        // Arrange
        String token = "sample.jwt.token";
        String expectedClaim = "some-claim";
        Function<Claims, String> claimsResolver = claims -> expectedClaim;
        when(jwtService.extractClaim(eq(token), any(Function.class))).thenReturn(expectedClaim);

        // Act
        String actualClaim = jwtService.extractClaim(token, claimsResolver);

        // Assert
        assertEquals(expectedClaim, actualClaim, "The extracted claim should match the expected value.");
        verify(jwtService, times(1)).extractClaim(eq(token), any(Function.class));
    }

    @Test
    void testExtractAllClaims() {
        // Arrange
        String token = "sample.jwt.token";
        when(jwtService.extractAllClaims(token)).thenReturn(claims);

        // Act
        Claims actualClaims = jwtService.extractAllClaims(token);

        // Assert
        assertNotNull(actualClaims, "Claims should not be null.");
        verify(jwtService, times(1)).extractAllClaims(token);
    }

    @Test
    void testGenerateToken() {
        // Arrange
        String userEmail = "user@example.com";
        String expectedToken = "generated.jwt.token";
        when(jwtService.generateToken(userEmail)).thenReturn(expectedToken);

        // Act
        String actualToken = jwtService.generateToken(userEmail);

        // Assert
        assertEquals(expectedToken, actualToken, "The generated token should match the expected value.");
        verify(jwtService, times(1)).generateToken(userEmail);
    }

    @Test
    void testIsTokenValid() {
        // Arrange
        String token = "sample.jwt.token";
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid, "The token should be valid.");
        verify(jwtService, times(1)).isTokenValid(token, userDetails);
    }

    @Test
    void testIsTokenInvalid() {
        // Arrange
        String token = "invalid.jwt.token";
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertFalse(isValid, "The token should be invalid.");
        verify(jwtService, times(1)).isTokenValid(token, userDetails);
    }
}
