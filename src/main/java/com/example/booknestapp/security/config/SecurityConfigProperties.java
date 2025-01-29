package com.example.booknestapp.security.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(value = "booknestapp.security")
public record SecurityConfigProperties(Config config) {
    public record Config(List<String> permitAllUrl) {}
}
