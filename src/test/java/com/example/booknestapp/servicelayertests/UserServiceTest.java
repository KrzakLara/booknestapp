package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.dto.UserDto;
import com.example.booknestapp.entity.User;
import com.example.booknestapp.security.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser() {
        // Arrange
        UserDto userDto = new UserDto(null, "John", "Doe", "john.doe@example.com", "password");

        doNothing().when(userService).saveUser(userDto);

        // Act
        userService.saveUser(userDto);

        // Assert
        verify(userService, times(1)).saveUser(userDto);
    }

    @Test
    void testFindByEmail() {
        // Arrange
        String email = "john.doe@example.com";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail(email);

        when(userService.findByEmail(email)).thenReturn(mockUser);

        // Act
        User foundUser = userService.findByEmail(email);

        // Assert
        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
        verify(userService, times(1)).findByEmail(email);
    }

    @Test
    void testFindAllUsers() {
        // Arrange
        List<UserDto> users = Arrays.asList(
                new UserDto(1L, "John", "Doe", "john.doe@example.com", "password"),
                new UserDto(2L, "Jane", "Smith", "jane.smith@example.com", "password123")
        );

        when(userService.findAllUsers()).thenReturn(users);

        // Act
        List<UserDto> foundUsers = userService.findAllUsers();

        // Assert
        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void testUpdateUser() {
        // Arrange
        UserDto userDto = new UserDto(1L, "John", "Doe", "john.doe@example.com", "newpassword");

        doNothing().when(userService).updateUser(userDto);

        // Act
        userService.updateUser(userDto);

        // Assert
        verify(userService, times(1)).updateUser(userDto);
    }

    @Test
    void testFindAllUsersWithBooks() {
        // Arrange
        List<UserDto> usersWithBooks = Arrays.asList(
                new UserDto(1L, "John", "Doe", "john.doe@example.com", "password"),
                new UserDto(2L, "Jane", "Smith", "jane.smith@example.com", "password123")
        );

        when(userService.findAllUsersWithBooks()).thenReturn(usersWithBooks);

        // Act
        List<UserDto> result = userService.findAllUsersWithBooks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userService, times(1)).findAllUsersWithBooks();
    }

    @Test
    void testGetAllBooks() {
        // Arrange
        List<BookDto> books = Arrays.asList(
                new BookDto(1L, "Book One", "Author One", "Description One", 10.99, true),
                new BookDto(2L, "Book Two", "Author Two", "Description Two", 15.99, false)
        );

        when(userService.getAllBooks()).thenReturn(books);

        // Act
        List<BookDto> result = userService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userService, times(1)).getAllBooks();
    }
}
