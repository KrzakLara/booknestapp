package com.example.booknestapp.dtotests;

import com.example.booknestapp.dto.BookResultDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookResultDtoTest {

    @Test
    void testBookResultDtoConstructorAndGetters() {
        // Create a BookResultDto instance
        Long id = 1L;
        String title = "Test Book Title";
        String author = "Test Author";
        String description = "Test Description";

        BookResultDto bookResultDto = new BookResultDto(id, title, author, description);

        // Assertions to verify the values
        assertNotNull(bookResultDto, "BookResultDto should not be null");
        assertEquals(id, bookResultDto.id(), "ID should match");
        assertEquals(title, bookResultDto.title(), "Title should match");
        assertEquals(author, bookResultDto.author(), "Author should match");
        assertEquals(description, bookResultDto.description(), "Description should match");
    }

    @Test
    void testBookResultDtoEqualityAndHashcode() {
        // Create two instances with the same values
        BookResultDto bookResultDto1 = new BookResultDto(1L, "Test Book Title", "Test Author", "Test Description");
        BookResultDto bookResultDto2 = new BookResultDto(1L, "Test Book Title", "Test Author", "Test Description");

        // Assertions to verify equality and hashcode
        assertEquals(bookResultDto1, bookResultDto2, "BookResultDto objects should be equal");
        assertEquals(bookResultDto1.hashCode(), bookResultDto2.hashCode(), "Hashcodes should match");
    }
}
