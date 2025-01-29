package com.example.booknestapp.security.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "booknestapp.auth.jwt")
public record JwtAuthConfigProperties(Config config) {
    public record Config(String secretKey, Long expirationTime, Long refreshTokenExpirationTime) {}
}
