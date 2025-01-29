package com.example.booknestapp.dtotests;

import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.dto.BookResponseInfoDto;
import com.example.booknestapp.entity.Book;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookResponseInfoDtoTest {

    @Test
    void testConstructorWithBook() {
        // Create a Book object and populate its fields
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPrice(19.99);
        book.setForSale(true);

        boolean hasResponded = true;

        // Create BookResponseInfoDto using the Book constructor
        BookResponseInfoDto dto = new BookResponseInfoDto(book, hasResponded);

        // Assertions
        assertNotNull(dto, "BookResponseInfoDto should not be null");
        assertEquals(1L, dto.getId(), "Book ID should match");
        assertEquals("Test Book", dto.getTitle(), "Book title should match");
        assertEquals("Test Author", dto.getAuthor(), "Book author should match");
        assertEquals(19.99, dto.getPrice(), "Book price should match");
        assertTrue(dto.isForSale(), "Book should be for sale");
        assertTrue(dto.isHasResponded(), "Response status should match");
    }

    @Test
    void testConstructorWithBookDto() {
        // Use the constructor for BookDto with all necessary fields
        BookDto bookDto = new BookDto(2L, "Another Test Book", "Another Test Author", "Description here", 29.99, false);

        boolean hasResponded = false;

        // Create BookResponseInfoDto using the BookDto constructor
        BookResponseInfoDto dto = new BookResponseInfoDto(bookDto, hasResponded);

        // Assertions
        assertNotNull(dto, "BookResponseInfoDto should not be null");
        assertEquals(2L, dto.getId(), "BookDto ID should match");
        assertEquals("Another Test Book", dto.getTitle(), "BookDto title should match");
        assertEquals("Another Test Author", dto.getAuthor(), "BookDto author should match");
        assertEquals(29.99, dto.getPrice(), "BookDto price should match");
        assertFalse(dto.isForSale(), "BookDto should not be for sale");
        assertFalse(dto.isHasResponded(), "Response status should match");
    }
}
