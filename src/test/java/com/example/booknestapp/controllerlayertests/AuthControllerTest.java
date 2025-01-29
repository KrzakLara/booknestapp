package com.example.booknestapp.controllerlayertests;

import com.example.booknestapp.controller.AuthController;
import com.example.booknestapp.dto.LoginRequestDto;
import com.example.booknestapp.dto.UserDto;
import com.example.booknestapp.entity.User;
import com.example.booknestapp.model.Role;
import com.example.booknestapp.security.service.JwtService;
import com.example.booknestapp.security.service.UserService;
import com.example.booknestapp.security.service.config.JwtAuthConfigPropertiesProvider;
import com.example.booknestapp.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BookService bookService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    @Mock
    private JwtAuthConfigPropertiesProvider jwtAuthConfigPropertiesProvider;

    // Original Tests

    @Test
    void testRegistrationSuccess() {
        UserDto userDto = new UserDto(null, "John", "Doe", "john@example.com", "password");
        when(userService.findByEmail("john@example.com")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<String> response = authController.registration(userDto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registration successful. Redirecting to login.", response.getBody());
        verify(userService).saveUser(userDto);
    }

    @Test
    void testLoginInvalidCredentials() {
        LoginRequestDto loginRequest = new LoginRequestDto("unknown@example.com", "password");
        when(userService.findByEmail("unknown@example.com")).thenReturn(null);

        String result = authController.login(loginRequest, response, model);

        assertEquals("login", result);
        verify(model).addAttribute("error", "Invalid email or password");
    }

    @Test
    void testUpdateUserSuccess() {
        UserDto userDto = new UserDto(1L, "John", "Doe", "john@example.com", "password");
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = authController.updateUser(userDto, bindingResult, model);

        assertEquals("redirect:/home", result);
        verify(userService).updateUser(userDto);
    }

    @Test
    void testAdminHome() {
        when(bookService.getAllBooks()).thenReturn(List.of());
        when(userService.findAllUsersWithBooks()).thenReturn(List.of(new UserDto()));

        String result = authController.adminHome(model);

        assertEquals("adminHome", result);
        verify(model).addAttribute(eq("users"), anyList());
    }

    @Test
    void testRefreshTokenInvalid() {
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<?> responseEntity = authController.refreshToken(request, response);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Refresh token not provided.", responseEntity.getBody());
    }

    // New Tests

    @Test
    void testRegistrationWithExistingEmail() {
        UserDto userDto = new UserDto(null, "John", "Doe", "john@example.com", "password");
        when(userService.findByEmail("john@example.com")).thenReturn(new User());
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<String> response = authController.registration(userDto, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed. Please check the input fields.", response.getBody());
    }

    @Test
    void testLoginSuccess() {
        // Mock input login request
        LoginRequestDto loginRequest = new LoginRequestDto("user@example.com", "password");

        // Use an existing enum constant for Role
        Role role = Role.USER;

        // Create a mock user
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setRole(role);

        // Mock JwtAuthConfigPropertiesProvider behavior
        when(jwtAuthConfigPropertiesProvider.getExpirationTime()).thenReturn(3600L); // Mock token expiration

        // Mock UserService and JwtService behavior
        when(userService.findByEmail("user@example.com")).thenReturn(user);
        when(jwtService.generateToken("user@example.com")).thenReturn("mockToken");

        // Execute the method
        String result = authController.login(loginRequest, response, model);

        // Verify the result
        assertEquals("redirect:/home", result);

        // Verify cookie is added
        verify(response).addCookie(any(Cookie.class));
    }


    @Test
    void testUpdateUserWithErrors() {
        UserDto userDto = new UserDto(1L, "John", "Doe", "john@example.com", "password");
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = authController.updateUser(userDto, bindingResult, model);

        assertEquals("editUser", result);
        verify(model).addAttribute("user", userDto);
    }

    @Test
    void testAdminHomeWithBooks() {
        List<UserDto> users = List.of(new UserDto(1L, "John", "Doe", "john@example.com", null));
        when(userService.findAllUsersWithBooks()).thenReturn(users);
        when(bookService.getAllBooks()).thenReturn(List.of());

        String result = authController.adminHome(model);

        assertEquals("adminHome", result);
        verify(model).addAttribute("users", users);
        verify(model).addAttribute("books", List.of());
    }

    @Test
    void testRefreshTokenWithInvalidToken() {
        Cookie cookie = new Cookie("jwt", "invalidToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtService.extractEmail("invalidToken")).thenThrow(new RuntimeException("Invalid token"));

        ResponseEntity<?> responseEntity = authController.refreshToken(request, response);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Invalid refresh token.", responseEntity.getBody());
    }

    @Test
    void testRefreshTokenValid() {
        Cookie cookie = new Cookie("jwt", "validToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtService.extractEmail("validToken")).thenReturn("user@example.com");
        when(jwtService.isTokenValid(eq("validToken"), any())).thenReturn(true);
        when(jwtService.generateToken("user@example.com")).thenReturn("newToken");

        ResponseEntity<?> responseEntity = authController.refreshToken(request, response);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Access token refreshed successfully.", responseEntity.getBody());
    }
}
