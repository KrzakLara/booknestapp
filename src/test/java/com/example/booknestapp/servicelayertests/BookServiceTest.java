package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.entity.Book;
import com.example.booknestapp.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBook() {
        // Arrange
        String title = "Book Title";
        String author = "Author Name";
        String description = "Book Description";
        String creatorEmail = "creator@example.com";
        double price = 19.99;

        when(bookService.createBook(title, author, description, creatorEmail, price)).thenReturn(true);

        // Act
        boolean result = bookService.createBook(title, author, description, creatorEmail, price);

        // Assert
        assertTrue(result);
        verify(bookService, times(1)).createBook(title, author, description, creatorEmail, price);
    }

    @Test
    void testGetAllBooks() {
        // Arrange
        List<BookDto> books = Arrays.asList(
                new BookDto(1L, "Book One", "Author One", "Description One", 10.99, true),
                new BookDto(2L, "Book Two", "Author Two", "Description Two", 15.99, false)
        );
        when(bookService.getAllBooks()).thenReturn(books);

        // Act
        List<BookDto> result = bookService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void testGetBookById() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookService.getBookById(1L)).thenReturn(book);

        // Act
        Book result = bookService.getBookById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void testDeleteBookById() {
        // Arrange
        doNothing().when(bookService).deleteBookById(1L);

        // Act
        bookService.deleteBookById(1L);

        // Assert
        verify(bookService, times(1)).deleteBookById(1L);
    }

    @Test
    void testEditBook() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Old Title");

        doNothing().when(bookService).editBook(book);

        // Act
        bookService.editBook(book);

        // Assert
        verify(bookService, times(1)).editBook(book);
    }

    @Test
    void testGetBooksByUser() {
        // Arrange
        String email = "user@example.com";
        List<BookDto> books = Arrays.asList(
                new BookDto(1L, "Book One", "Author One", "Description One", 10.99, true),
                new BookDto(2L, "Book Two", "Author Two", "Description Two", 15.99, false)
        );
        when(bookService.getBooksByUser(email)).thenReturn(books);

        // Act
        List<BookDto> result = bookService.getBooksByUser(email);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookService, times(1)).getBooksByUser(email);
    }

    @Test
    void testMarkAsForSale() {
        // Arrange
        doNothing().when(bookService).markAsForSale(1L);

        // Act
        bookService.markAsForSale(1L);

        // Assert
        verify(bookService, times(1)).markAsForSale(1L);
    }

    @Test
    void testBuyBook() {
        // Arrange
        doNothing().when(bookService).buyBook(1L);

        // Act
        bookService.buyBook(1L);

        // Assert
        verify(bookService, times(1)).buyBook(1L);
    }

    @Test
    void testMarkBookForSale() {
        // Arrange
        doNothing().when(bookService).markBookForSale(1L, true);

        // Act
        bookService.markBookForSale(1L, true);

        // Assert
        verify(bookService, times(1)).markBookForSale(1L, true);
    }

    @Test
    void testMarkAsSold() {
        // Arrange
        doNothing().when(bookService).markAsSold(1L);

        // Act
        bookService.markAsSold(1L);

        // Assert
        verify(bookService, times(1)).markAsSold(1L);
    }

    @Test
    void testFindAllBooks() {
        // Arrange
        List<BookDto> books = Arrays.asList(
                new BookDto(1L, "Book One", "Author One", "Description One", 10.99, true),
                new BookDto(2L, "Book Two", "Author Two", "Description Two", 15.99, false)
        );
        when(bookService.findAllBooks()).thenReturn(books);

        // Act
        List<BookDto> result = bookService.findAllBooks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookService, times(1)).findAllBooks();
    }
}
