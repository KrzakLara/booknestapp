package com.example.booknestapp.security.service;

import com.example.booknestapp.security.service.config.JwtAuthConfigPropertiesProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class IJwtService implements JwtService {

    private final JwtAuthConfigPropertiesProvider jwtAuthConfigPropertiesProvider;
    private final CustomUserDetailsService customUserDetailsService;


    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtAuthConfigPropertiesProvider.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate Access Token
    @Override
    public String generateToken(String userEmail) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        Map<String, Object> claims = new HashMap<>();

        // Add roles to claims
        var roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roles);

        return createToken(claims, userEmail, jwtAuthConfigPropertiesProvider.getExpirationTime());
    }

    // Generate Refresh Token
    @Override
    public String generateRefreshToken(String userEmail) {
        return createToken(new HashMap<>(), userEmail, refreshTokenExpirationTime());
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        var currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private long refreshTokenExpirationTime() {
        // Refresh token expiration time (e.g., 7 days)
        return jwtAuthConfigPropertiesProvider.getRefreshTokenExpirationTime();
    }

    // Validate Access Token
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Validate Refresh Token
    @Override
    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
