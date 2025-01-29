package com.example.booknestapp.security.filter;

import com.example.booknestapp.security.service.AuthenticationService;
import com.example.booknestapp.security.service.CustomUserDetailsService;
import com.example.booknestapp.security.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    @Qualifier("customUserDetailsService")
    private final UserDetailsService userDetailsService;
    private final AuthenticationService authenticationService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String jwt;
        final String userEmail;

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestUri = request.getRequestURI();

        if ("/".equals(requestUri)) {
            if (isJwtPresentAndValid(request)) {
                response.sendRedirect("/home");
            } else {
                redirectToLogin(response);
            }
            return;
        }

        String[] requestUriParts = requestUri.split("/");
        if (authenticationService.checkIsPermitAll(getFirstUriPart(requestUriParts))) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie jwtCookie = getJwtCookie(request.getCookies());
        if (jwtCookie == null) {
            redirectToLogin(response);
            return;
        }

        jwt = jwtCookie.getValue();
        try {
            userEmail = jwtService.extractEmail(jwt);
        } catch (Exception ex) {
            redirectToLogin(response);
            return;
        }

        if (userEmail == null || !isUserAuthenticated(jwt, userEmail)) {
            redirectToLogin(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isJwtPresentAndValid(HttpServletRequest request) {
        Cookie jwtCookie = getJwtCookie(request.getCookies());
        if (jwtCookie == null) {
            return false;
        }

        String jwt = jwtCookie.getValue();
        String userEmail;
        try {
            userEmail = jwtService.extractEmail(jwt);
        } catch (Exception ex) {
            return false;
        }

        return userEmail != null && isUserAuthenticated(jwt, userEmail);
    }

    private String getFirstUriPart(String[] uriParts) {
        return (uriParts.length > 1) ? uriParts[1] : "";
    }

    private Cookie getJwtCookie(Cookie[] requestCookieArray) {
        if (requestCookieArray == null || requestCookieArray.length == 0) {
            return null;
        }
        return Stream.of(requestCookieArray)
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .findFirst()
                .orElse(null);
    }

    private boolean isUserAuthenticated(String jwt, String userEmail) {
        try {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                Claims claims = jwtService.extractAllClaims(jwt);


                @SuppressWarnings("unchecked")
                var roles = (List<String>) claims.get("roles");



                var authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();


                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(),
                        null,
                        authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            return false;
        }
        return false;
    }


    private void redirectToLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login");
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestUri = request.getRequestURI();
        return requestUri.startsWith("/public/") || requestUri.equals("/login");
    }

}
