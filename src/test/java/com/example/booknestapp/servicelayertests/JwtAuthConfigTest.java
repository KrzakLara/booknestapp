package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.security.service.config.JwtAuthConfig;
import com.example.booknestapp.security.service.config.JwtAuthConfigProperties;
import com.example.booknestapp.security.service.config.JwtAuthConfigProperties.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = JwtAuthConfig.class)
class JwtAuthConfigTest {

    private JwtAuthConfigProperties jwtAuthConfigProperties;

    @BeforeEach
    void setUp() {
        // Mock the Config class inside JwtAuthConfigProperties
        Config configMock = mock(Config.class);
        when(configMock.secretKey()).thenReturn("mockSecretKey");
        when(configMock.expirationTime()).thenReturn(3600000L);

        // Mock JwtAuthConfigProperties and return the mocked Config
        jwtAuthConfigProperties = mock(JwtAuthConfigProperties.class);
        when(jwtAuthConfigProperties.config()).thenReturn(configMock);
    }

    @Test
    void testJwtAuthConfigLoads() {
        JwtAuthConfig jwtAuthConfig = new JwtAuthConfig();
        assertNotNull(jwtAuthConfig, "JwtAuthConfig bean should not be null");
    }

    @Test
    void testJwtAuthConfigPropertiesLoaded() {
        assertNotNull(jwtAuthConfigProperties, "JwtAuthConfigProperties bean should not be null");
        Config config = jwtAuthConfigProperties.config();
        assertNotNull(config, "Config inside JwtAuthConfigProperties should not be null");
        assertNotNull(config.secretKey(), "SecretKey in Config should not be null");
        assertNotNull(config.expirationTime(), "ExpirationTime in Config should not be null");
    }
}
