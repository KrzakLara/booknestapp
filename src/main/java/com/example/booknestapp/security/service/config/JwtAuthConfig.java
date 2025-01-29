package com.example.booknestapp.security.service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtAuthConfigProperties.class)
public class JwtAuthConfig {
}
