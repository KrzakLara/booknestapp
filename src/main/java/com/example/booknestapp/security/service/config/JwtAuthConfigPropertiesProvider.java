package com.example.booknestapp.security.service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthConfigPropertiesProvider {

    private final JwtAuthConfigProperties jwtAuthConfigProperties;

    public JwtAuthConfigProperties.Config getConfig() {
        return jwtAuthConfigProperties.config();
    }

    public String getSecretKey() {
        return getConfig().secretKey();
    }

    public Long getExpirationTime() {
        return getConfig().expirationTime();
    }

    public Long getRefreshTokenExpirationTime() {
        return getConfig().refreshTokenExpirationTime();
    }
}
