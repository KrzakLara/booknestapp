package com.example.booknestapp.security.service;

import com.example.booknestapp.security.config.SecurityConfigPropertiesProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IAuthenticationService implements AuthenticationService {

    private final SecurityConfigPropertiesProvider securityConfigPropertiesProvider;

    @Override
    public String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public boolean checkIsPermitAll(String uriFirst) {
        return securityConfigPropertiesProvider.getPermitAllUrl()
                .stream().map(path -> path.replace("*",""))
                .anyMatch(path -> path.contains(uriFirst));
    }
}
