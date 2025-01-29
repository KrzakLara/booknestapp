package com.example.booknestapp.security.config;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityConfigPropertiesProvider {
    private final SecurityConfigProperties securityConfigProperties;

    public SecurityConfigProperties.Config getConfig() {
        return securityConfigProperties.config();
    }

    public List<String> getPermitAllUrl() {
        return getConfig().permitAllUrl();
    }
}
