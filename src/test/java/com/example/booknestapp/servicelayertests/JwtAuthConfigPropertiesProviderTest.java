package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.security.service.config.JwtAuthConfigProperties;
import com.example.booknestapp.security.service.config.JwtAuthConfigPropertiesProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthConfigPropertiesProviderTest {

    @Mock
    private JwtAuthConfigProperties jwtAuthConfigProperties;

    @Mock
    private JwtAuthConfigProperties.Config mockConfig;

    private JwtAuthConfigPropertiesProvider jwtAuthConfigPropertiesProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jwtAuthConfigProperties.config()).thenReturn(mockConfig);
        jwtAuthConfigPropertiesProvider = new JwtAuthConfigPropertiesProvider(jwtAuthConfigProperties);
    }

    @Test
    void testGetConfig() {
        // Act
        JwtAuthConfigProperties.Config config = jwtAuthConfigPropertiesProvider.getConfig();

        // Assert
        assertNotNull(config, "Config should not be null");
        verify(jwtAuthConfigProperties, times(1)).config();
    }

    @Test
    void testGetSecretKey() {
        // Arrange
        String secretKey = "test-secret-key";
        when(mockConfig.secretKey()).thenReturn(secretKey);

        // Act
        String result = jwtAuthConfigPropertiesProvider.getSecretKey();

        // Assert
        assertEquals(secretKey, result, "Secret key should match");
        verify(mockConfig, times(1)).secretKey();
    }

    @Test
    void testGetExpirationTime() {
        // Arrange
        Long expirationTime = 3600L;
        when(mockConfig.expirationTime()).thenReturn(expirationTime);

        // Act
        Long result = jwtAuthConfigPropertiesProvider.getExpirationTime();

        // Assert
        assertEquals(expirationTime, result, "Expiration time should match");
        verify(mockConfig, times(1)).expirationTime();
    }

    @Test
    void testGetRefreshTokenExpirationTime() {
        // Arrange
        Long refreshTokenExpirationTime = 7200L;
        when(mockConfig.refreshTokenExpirationTime()).thenReturn(refreshTokenExpirationTime);

        // Act
        Long result = jwtAuthConfigPropertiesProvider.getRefreshTokenExpirationTime();

        // Assert
        assertEquals(refreshTokenExpirationTime, result, "Refresh token expiration time should match");
        verify(mockConfig, times(1)).refreshTokenExpirationTime();
    }

    @Test
    void testProviderIntegration() {
        // Arrange
        String secretKey = "integration-secret-key";
        Long expirationTime = 3600L;
        Long refreshTokenExpirationTime = 7200L;

        when(mockConfig.secretKey()).thenReturn(secretKey);
        when(mockConfig.expirationTime()).thenReturn(expirationTime);
        when(mockConfig.refreshTokenExpirationTime()).thenReturn(refreshTokenExpirationTime);

        // Act
        String actualSecretKey = jwtAuthConfigPropertiesProvider.getSecretKey();
        Long actualExpirationTime = jwtAuthConfigPropertiesProvider.getExpirationTime();
        Long actualRefreshTokenExpirationTime = jwtAuthConfigPropertiesProvider.getRefreshTokenExpirationTime();

        // Assert
        assertEquals(secretKey, actualSecretKey, "Secret key should match");
        assertEquals(expirationTime, actualExpirationTime, "Expiration time should match");
        assertEquals(refreshTokenExpirationTime, actualRefreshTokenExpirationTime, "Refresh token expiration time should match");

        verify(mockConfig, times(1)).secretKey();
        verify(mockConfig, times(1)).expirationTime();
        verify(mockConfig, times(1)).refreshTokenExpirationTime();
    }


}
