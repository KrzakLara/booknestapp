package com.example.booknestapp.controllerlayertests;

import com.example.booknestapp.controller.StartController;
import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.entity.User;
import com.example.booknestapp.security.service.AuthenticationService;
import com.example.booknestapp.security.service.CustomUserDetailsService;
import com.example.booknestapp.security.service.JwtService;
import com.example.booknestapp.security.service.UserService;
import com.example.booknestapp.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = StartController.class)
@Import(StartControllerTest.TestConfig.class) // Import test configuration
class StartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        Mockito.reset(authenticationService, userService, bookService, jwtService, customUserDetailsService);
    }

    @Test
    @WithMockUser // Simulate an authenticated user
    void testHomePage() throws Exception {
        String email = "user@example.com";

        // Mock User
        User mockUser = new User(
                1L, // ID
                "John", // First Name
                "Doe", // Last Name
                email, // Email
                "password", // Password
                null // Role (adjust if necessary)
        );

        // Mock dependencies
        when(authenticationService.getAuthenticatedUserEmail()).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(mockUser); // Mock User returned by `findByEmail`
        when(bookService.getBooksByUser(email)).thenReturn(Collections.emptyList());

        // Perform GET request to /home
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk()) // HTTP status 200
                .andExpect(view().name("home")) // View name
                .andExpect(model().attributeExists("user")) // Model contains "user"
                .andExpect(model().attribute("user", mockUser)) // Ensure "user" matches mockUser
                .andExpect(model().attributeExists("pollResponseInfo")) // Model contains "pollResponseInfo"
                .andExpect(model().attribute("pollResponseInfo", Collections.emptyList())); // Ensure books list is empty
    }

    /**
     * Test configuration to mock security and service beans.
     */
    @TestConfiguration
    static class TestConfig {

        @Bean
        public AuthenticationService authenticationService() {
            return Mockito.mock(AuthenticationService.class);
        }

        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public BookService bookService() {
            return Mockito.mock(BookService.class);
        }

        @Bean
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }

        @Bean
        public CustomUserDetailsService customUserDetailsService() {
            return Mockito.mock(CustomUserDetailsService.class);
        }
    }
}
