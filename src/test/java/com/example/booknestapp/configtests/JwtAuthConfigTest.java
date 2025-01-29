package com.example.booknestapp.configtests;

import com.example.booknestapp.security.service.config.JwtAuthConfig;
import com.example.booknestapp.security.service.config.JwtAuthConfigProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtAuthConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @Test
    void testJwtAuthConfigLoads() {
        contextRunner.run(context -> {
            JwtAuthConfig jwtAuthConfig = context.getBean(JwtAuthConfig.class);
            assertNotNull(jwtAuthConfig, "JwtAuthConfig bean should be loaded");
        });
    }

    @Test
    void testJwtAuthConfigPropertiesLoads() {
        contextRunner.run(context -> {
            JwtAuthConfigProperties jwtAuthConfigProperties = context.getBean(JwtAuthConfigProperties.class);
            assertNotNull(jwtAuthConfigProperties, "JwtAuthConfigProperties bean should be loaded");
        });
    }

    @Configuration
    static class TestConfig {

        @Bean
        public JwtAuthConfig jwtAuthConfig() {
            return new JwtAuthConfig();
        }

        @Bean
        public JwtAuthConfigProperties jwtAuthConfigProperties() {
            return new JwtAuthConfigProperties(
                    new JwtAuthConfigProperties.Config(
                            "secretKey",       // Secret Key
                            3600L,             // Access Token Expiration Time (e.g., 1 hour)
                            604800000L         // Refresh Token Expiration Time (e.g., 7 days)
                    )
            );
        }
    }
}
