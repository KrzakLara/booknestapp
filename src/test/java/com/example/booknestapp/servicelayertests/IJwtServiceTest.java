package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.security.service.IJwtService;
import com.example.booknestapp.security.service.CustomUserDetailsService;
import com.example.booknestapp.security.service.config.JwtAuthConfigPropertiesProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.security.KeyPair;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IJwtServiceTest {
    @Mock
    private JwtAuthConfigPropertiesProvider jwtAuthConfigPropertiesProvider;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    private IJwtService jwtService;
    private String validBase64Key;
    private Long expirationTime;
    private Long refreshExpirationTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Generate a valid 256-bit signing key
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        validBase64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        expirationTime = 3600000L; // 1 hour
        refreshExpirationTime = 604800000L; // 7 days

        // Mock JwtAuthConfigPropertiesProvider
        when(jwtAuthConfigPropertiesProvider.getSecretKey()).thenReturn(validBase64Key);
        when(jwtAuthConfigPropertiesProvider.getExpirationTime()).thenReturn(expirationTime);
        when(jwtAuthConfigPropertiesProvider.getRefreshTokenExpirationTime()).thenReturn(refreshExpirationTime);

        // Use a spy for jwtService
        jwtService = spy(new IJwtService(jwtAuthConfigPropertiesProvider, customUserDetailsService));
    }


    @Test
    void testGenerateToken() {
        String email = "test@example.com";
        UserDetails userDetails = createMockUser(email);

        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        String token = jwtService.generateToken(email);

        assertNotNull(token, "Generated token should not be null");
        assertDoesNotThrow(() -> jwtService.extractEmail(token), "Extracting email should not throw an exception");
        assertEquals(email, jwtService.extractEmail(token), "Extracted email should match the original email");
    }

    @Test
    void testGenerateRefreshToken() {
        String email = "test@example.com";

        String refreshToken = jwtService.generateRefreshToken(email);

        assertNotNull(refreshToken, "Generated refresh token should not be null");
        assertDoesNotThrow(() -> jwtService.extractEmail(refreshToken), "Extracting email from refresh token should not throw an exception");
        assertEquals(email, jwtService.extractEmail(refreshToken), "Extracted email should match the original email");
    }

    @Test
    void testIsTokenValid() {
        String email = "test@example.com";
        UserDetails userDetails = createMockUser(email);

        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        String token = jwtService.generateToken(email);

        assertTrue(jwtService.isTokenValid(token, userDetails), "Token should be valid for the user");
    }

    @Test
    void testIsRefreshTokenValid() {
        String email = "test@example.com";
        UserDetails userDetails = createMockUser(email);

        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        String refreshToken = jwtService.generateRefreshToken(email);

        assertTrue(jwtService.isRefreshTokenValid(refreshToken, userDetails), "Refresh token should be valid for the user");
    }

    @Test
    void testExtractAllClaims() {
        String email = "test@example.com";
        UserDetails userDetails = createMockUser(email);

        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        String token = jwtService.generateToken(email);
        Claims claims = jwtService.extractAllClaims(token);

        assertNotNull(claims, "Claims should not be null");
        assertEquals(email, claims.getSubject(), "Subject should match the email");
        assertNotNull(claims.get("roles"), "Roles claim should exist");
    }

    @Test
    void testIsTokenExpired() {
        String email = "test@example.com";
        UserDetails userDetails = createMockUser(email);

        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        String token = jwtService.generateToken(email);

        assertFalse(jwtService.isTokenExpired(token), "Token should not be expired immediately after generation");
    }

    @Test
    void testInvalidTokenSignature() {
        // Arrange
        String email = "test@example.com";
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiZXhwIjoxNjYwMDAwMDAwfQ.INVALID_SIGNATURE";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtService.extractEmail(invalidToken), "Invalid token signature should throw an exception");
    }

    private UserDetails createMockUser(String email) {
        return new User(email, "password", Collections.singletonList(() -> "ROLE_USER"));
    }

    @Test
    void testIsTokenExpired_withCustomExpiredToken() {
        // Arrange: Set up a valid Base64 key and mock necessary methods
        String email = "test@example.com";
        UserDetails userDetails = createMockUser(email);

        // Mock dependencies
        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtAuthConfigPropertiesProvider.getSecretKey()).thenReturn(validBase64Key);
        when(jwtAuthConfigPropertiesProvider.getExpirationTime()).thenReturn(1000L); // 1 second expiration

        // Generate token
        String token = jwtService.generateToken(email);

        // Simulate time passage by mocking the expiration check
        Date pastExpirationDate = new Date(System.currentTimeMillis() - 2000); // 2 seconds in the past
        doReturn(pastExpirationDate).when(jwtService).extractExpiration(token);

        // Act & Assert: The token should be considered expired
        assertTrue(jwtService.isTokenExpired(token), "Token should be expired based on mocked past expiration date");
    }


    @Test
    void testExtractAllClaimsWithInvalidKey() {
        // Arrange
        String email = "test@example.com";
        UserDetails userDetails = createMockUser(email);

        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        String validToken = jwtService.generateToken(email);

        // Mock an invalid but sufficiently long key
        String invalidKey = "thisisaveryinvalidkeythatissufficientlylongbutinvalid123456";
        when(jwtAuthConfigPropertiesProvider.getSecretKey()).thenReturn(invalidKey);

        // Act & Assert
        assertThrows(io.jsonwebtoken.security.SignatureException.class, () -> jwtService.extractAllClaims(validToken),
                "Extracting claims with an invalid key should throw a SignatureException");
    }
    @Test
    void testIsRefreshTokenExpired() {
        // Arrange: Set up a valid token and mock the expiration check
        String email = "test@example.com";
        UserDetails userDetails = createMockUser(email);

        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // Generate a refresh token
        String refreshToken = jwtService.generateRefreshToken(email);

        // Simulate time passage to expire the token
        doReturn(new Date(System.currentTimeMillis() - 2000)) // 2 seconds ago
                .when(jwtService).extractExpiration(refreshToken);

        // Act & Assert
        assertTrue(jwtService.isTokenExpired(refreshToken), "Refresh token should be expired");
    }

    /**
     * Test case for invalid token structure
     */
    @Test
    void testExtractEmailWithMalformedToken() {
        // Arrange: Use a malformed token
        String malformedToken = "malformed.token.structure";

        // Act & Assert
        assertThrows(io.jsonwebtoken.MalformedJwtException.class,
                () -> jwtService.extractEmail(malformedToken),
                "Malformed token should throw a MalformedJwtException");
    }

    @Test
    void testExtractClaimsWithUnsupportedAlgorithm() {
        // Arrange: Generate an elliptic curve key for ES256
        KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.ES256); // Generate a key pair for ES256
        String unsupportedToken = Jwts.builder()
                .setSubject("test@example.com")
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.ES256) // Sign with ES256
                .compact();

        // Act & Assert: Ensure it throws UnsupportedJwtException when parsed with HS256
        assertThrows(io.jsonwebtoken.UnsupportedJwtException.class,
                () -> jwtService.extractAllClaims(unsupportedToken),
                "Tokens with unsupported algorithms should throw UnsupportedJwtException");
    }

    @Test
    void testExtractEmailWithNullToken() {
        // Act & Assert: Ensure null tokens throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
                () -> jwtService.extractEmail(null),
                "Null token should throw IllegalArgumentException");
    }

    @Test
    void testExtractEmailWithEmptyToken() {
        // Act & Assert: Ensure empty tokens throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
                () -> jwtService.extractEmail(""),
                "Empty token should throw IllegalArgumentException");
    }

    @Test
    void testExpiredTokenException() {
        // Arrange: Create an expired token
        String email = "test@example.com";
        String expiredToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Already expired
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(validBase64Key)), SignatureAlgorithm.HS256)
                .compact();

        // Act & Assert: Ensure expired tokens throw ExpiredJwtException
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.extractAllClaims(expiredToken),
                "Expired tokens should throw ExpiredJwtException");
    }
    @Test
    void testIsTokenValid_withMismatchedEmail() {
        // Arrange
        String email = "test@example.com";
        UserDetails userDetails = createMockUser("different@example.com"); // Different email
        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        String token = jwtService.generateToken(email);

        // Act & Assert
        assertFalse(jwtService.isTokenValid(token, userDetails), "Token should be invalid for mismatched emails");
    }

    @Test
    void testIsTokenValid_withExpiredToken() {
        // Arrange: Create a token with a short expiration time
        String email = "test@example.com";
        UserDetails userDetails = createMockUser(email);

        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtAuthConfigPropertiesProvider.getExpirationTime()).thenReturn(1000L); // 1 second expiration

        String expiredToken = jwtService.generateToken(email);

        // Simulate time passage
        try {
            Thread.sleep(2000); // Wait 2 seconds to ensure token is expired
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act & Assert
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.isTokenValid(expiredToken, userDetails),
                "Expired token should throw ExpiredJwtException");
    }

    @Test
    void testGenerateToken_withEmptyRoles() {
        // Arrange
        String email = "test@example.com";
        UserDetails userDetails = new User(email, "password", Collections.emptyList()); // No roles
        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // Act
        String token = jwtService.generateToken(email);

        // Assert
        assertNotNull(token, "Generated token should not be null, even with empty roles");
        Claims claims = jwtService.extractAllClaims(token);
        assertNotNull(claims.get("roles"), "Roles claim should not be null, even if roles are empty");
        assertTrue(((List<?>) claims.get("roles")).isEmpty(), "Roles claim should be an empty list for a user with no roles");
    }


}
