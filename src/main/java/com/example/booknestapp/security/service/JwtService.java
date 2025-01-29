package com.example.booknestapp.security.service;

import io.jsonwebtoken.Claims;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractEmail(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    Claims extractAllClaims(String token);

    String generateToken(String userEmail);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateRefreshToken(String userEmail);

    boolean isRefreshTokenValid(String token, UserDetails userDetails);

}
