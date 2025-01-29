package com.example.booknestapp.controllerlayertests;

import com.example.booknestapp.controller.UserController;
import com.example.booknestapp.dto.UserDto;
import com.example.booknestapp.entity.User;
import com.example.booknestapp.model.Role;
import com.example.booknestapp.security.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
    }

    @Test
    void testGetUserByEmail_Success() {
        String email = "john.doe@example.com";
        User user = new User(1L, "John", "Doe", email, "password123", Role.USER);
        UserDto userDto = new UserDto(1L, "John", "Doe", email, null);

        when(userService.findByEmail(email)).thenReturn(user);

        UserDto result = userController.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(userService).findByEmail(email);
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userService.findByEmail(email)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.getUserByEmail(email);
        });
        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
        verify(userService, times(1)).findByEmail(email);
    }

    @Test
    void testSaveUser() {
        UserDto userDto = new UserDto(1L, "John", "Doe", "john.doe@example.com", "password123");

        doNothing().when(userService).saveUser(userDto);

        String result = userController.saveUser(userDto);

        assertEquals("User saved successfully.", result);
        verify(userService, times(1)).saveUser(userDto);
    }

    @Test
    void testGetAllUsers() {
        List<UserDto> userList = List.of(
                new UserDto(1L, "John", "Doe", "john.doe@example.com", null),
                new UserDto(2L, "Jane", "Smith", "jane.smith@example.com", null)
        );

        when(userService.findAllUsers()).thenReturn(userList);

        List<UserDto> result = userController.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void testUpdateUserEmail_Success() {
        Long userId = 1L;
        String newEmail = "new.email@example.com";

        when(userService.updateUserEmail(userId, newEmail)).thenReturn(true);

        String result = userController.updateUserEmail(userId, newEmail);

        assertEquals("Email updated successfully", result);
        verify(userService, times(1)).updateUserEmail(userId, newEmail);
    }

    @Test
    void testUpdateUserEmail_Failure() {
        Long userId = 1L;
        String newEmail = "invalid.email@example.com";

        when(userService.updateUserEmail(userId, newEmail)).thenReturn(false);

        String result = userController.updateUserEmail(userId, newEmail);

        assertEquals("Failed to update email", result);
        verify(userService, times(1)).updateUserEmail(userId, newEmail);
    }

    @Test
    void testGetUsersByStatus_Success() {
        List<UserDto> users = List.of(
                new UserDto(1L, "John", "Doe", "john.doe@example.com", null),
                new UserDto(2L, "Jane", "Smith", "jane.smith@example.com", null)
        );

        when(userService.getUsersByStatus("active")).thenReturn(users);

        List<UserDto> result = userController.findUsersByFirstName("active");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userService, times(1)).getUsersByStatus("active");
    }

}
