package com.example.booknestapp.controller;

import com.example.booknestapp.cache.BookCache;
import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.dto.LoginRequestDto;
import com.example.booknestapp.dto.UserDto;
import com.example.booknestapp.dto.ValidationGroups;
import com.example.booknestapp.entity.User;
import com.example.booknestapp.model.Role;
import com.example.booknestapp.security.service.JwtService;
import com.example.booknestapp.security.service.config.JwtAuthConfigPropertiesProvider;
import com.example.booknestapp.security.service.UserService;
import com.example.booknestapp.service.BookService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthConfigPropertiesProvider jwtAuthConfigPropertiesProvider;
    private final BookService bookService;
    private final BookCache bookCache;



    @PostMapping("/public/api/register/save")
    public ResponseEntity<String> registration(@Validated(ValidationGroups.OnCreate.class) @RequestBody UserDto userBody,
                                               BindingResult result) {
        User existing = userService.findByEmail(userBody.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation failed. Please check the input fields.");
        }
        userService.saveUser(userBody);
        return ResponseEntity.ok("Registration successful. Redirecting to login.");
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto userBody = new UserDto();
        model.addAttribute("user", userBody);
        return "register";
    }

    @PostMapping("/public/api/auth/login")
    public String login(@RequestBody LoginRequestDto loginRequest, HttpServletResponse response, Model model) {
        // Find user by email
        var user = userService.findByEmail(loginRequest.email());
        if (user == null) {
            model.addAttribute("error", "Invalid email or password");
            return "login"; // Redirect back to login page with an error
        }

        // Authenticate the user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), loginRequest.password(), user.getAuthorities())
        );

        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail());
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(jwtAuthConfigPropertiesProvider.getExpirationTime().intValue());
        response.addCookie(jwtCookie);

    // Redirect to adminHome if the user is admin
        if ("admin@example.com".equalsIgnoreCase(user.getEmail())) {
        return "redirect:/adminHome";
    }

    // Redirect to home for other users
        return "redirect:/home";
}



    @GetMapping("/editUser")
    public String editUserForm(Model model) {
        var authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var currentUser = userService.findByEmail(authenticatedUserEmail);

        var userDto = new UserDto(
                currentUser.getId(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getEmail(),
                ""
        );

        model.addAttribute("user", userDto);
        return "editUser";
    }

    @PostMapping("/updateUser")
    public String updateUser(@Validated(ValidationGroups.OnUpdate.class) @ModelAttribute("user") UserDto userDto,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "editUser";
        }

        userService.updateUser(userDto);
        return "redirect:/home";
    }

    @GetMapping("/adminHome")
    public String adminHome(Model model) {
        List<UserDto> users = userService.findAllUsersWithBooks();
        model.addAttribute("users", users);


        List<BookDto> books = bookService.getAllBooks();
        model.addAttribute("books", books);

        return "adminHome";
    }

    @PostMapping("/public/api/auth/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie jwtCookie = getJwtCookie(request.getCookies());
        if (jwtCookie == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not provided.");
        }

        String refreshToken = jwtCookie.getValue();
        String userEmail;
        try {
            userEmail = jwtService.extractEmail(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
        }

        if (!jwtService.isTokenValid(refreshToken, userService.loadUserByUsername(userEmail))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token.");
        }


        // Generate a new token
        String newAccessToken = jwtService.generateToken(userEmail);

        Cookie newJwtCookie = new Cookie("jwt", newAccessToken);
        newJwtCookie.setHttpOnly(true);
        newJwtCookie.setSecure(false);
        newJwtCookie.setPath("/");
        newJwtCookie.setMaxAge(jwtAuthConfigPropertiesProvider.getExpirationTime().intValue());
        response.addCookie(newJwtCookie);

        return ResponseEntity.ok("Access token refreshed successfully.");
    }

    private Cookie getJwtCookie(Cookie[] cookies) {
        if (cookies == null || cookies.length == 0) return null;
        return Arrays.stream(cookies).filter(cookie -> "jwt".equals(cookie.getName())).findFirst().orElse(null);
    }

}
