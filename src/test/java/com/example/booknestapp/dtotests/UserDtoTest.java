package com.example.booknestapp.dtotests;

import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void testUserDtoConstructorAndGetters() {
        // Arrange
        BookDto book1 = new BookDto(1L, "Book 1", "Author 1", "Description 1", 10.99, true);
        BookDto book2 = new BookDto(2L, "Book 2", "Author 2", "Description 2", 12.99, false);
        List<BookDto> books = List.of(book1, book2);

        // Act
        UserDto userDto = new UserDto(1L, books, "John", "Doe", "john.doe@example.com", "password123");

        // Assert
        assertEquals(1L, userDto.getId());
        assertEquals(books, userDto.getBooks());
        assertEquals("John", userDto.getFirstName());
        assertEquals("Doe", userDto.getLastName());
        assertEquals("john.doe@example.com", userDto.getEmail());
        assertEquals("password123", userDto.getPassword());
    }

    @Test
    void testToString() {
        // Arrange
        UserDto userDto = new UserDto(2L, "Jane", "Smith", "jane.smith@example.com", "securepass");

        // Act
        String result = userDto.toString();

        // Assert
        assertTrue(result.contains("id=2"));
        assertTrue(result.contains("firstName=Jane"));
        assertTrue(result.contains("lastName=Smith"));
        assertTrue(result.contains("email=jane.smith@example.com"));
        assertTrue(result.contains("password=securepass"));
        assertTrue(result.contains("books=[]")); // Default empty list for books
    }
}
