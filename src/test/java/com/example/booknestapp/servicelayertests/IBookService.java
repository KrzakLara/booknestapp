package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.cache.BookCache;
import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.entity.Book;
import com.example.booknestapp.entity.User;
import com.example.booknestapp.repository.BookRepository;
import com.example.booknestapp.security.service.UserService;
import com.example.booknestapp.service.IBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IBookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookCache bookCache;

    private IBookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new IBookService(bookRepository, userService, bookCache);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userService.findByEmail("invalid@example.com")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.createBook("Title", "Author", "Description", "invalid@example.com", 100.0);
        });

        assertEquals("User with email invalid@example.com does not exist.", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookCache, never()).addBook(any(Book.class));
    }

    @Test
    void shouldCreateBookSuccessfully() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);

        Book book = new Book("Title", "Author", "Description", user, LocalDate.now(), false, 100.0);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        boolean result = bookService.createBook("Title", "Author", "Description", "test@example.com", 100.0);

        // Assert
        assertTrue(result);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookCache, times(1)).addBook(any(Book.class));
    }

    @Test
    void shouldNotCreateBookWithInvalidDescriptionLength() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(user);

        // Act
        boolean result = bookService.createBook(
                "Title",
                "Author",
                "a".repeat(256), // Invalid description length
                "test@example.com",
                100.0
        );

        // Assert
        assertFalse(result);
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookCache, never()).addBook(any(Book.class));
    }

    @Test
    void shouldRetrieveAllBooks() {
        // Arrange
        List<Book> books = new ArrayList<>();
        books.add(new Book("Title1", "Author1", "Description1", new User(), LocalDate.now(), false, 100.0));
        books.add(new Book("Title2", "Author2", "Description2", new User(), LocalDate.now(), true, 150.0));

        when(bookRepository.findAll()).thenReturn(books);

        // Act
        List<BookDto> bookDtos = bookService.getAllBooks();

        // Assert
        assertEquals(2, bookDtos.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void shouldRetrieveBookById() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        Book foundBook = bookService.getBookById(1L);

        // Assert
        assertNotNull(foundBook);
        assertEquals(1L, foundBook.getId());
    }

    @Test
    void shouldThrowExceptionWhenBookNotFoundById() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.getBookById(1L);
        });

        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void shouldDeleteBookByIdSuccessfully() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        // Act
        bookService.deleteBookById(1L);

        // Assert
        verify(bookRepository, times(1)).deleteById(1L);
        verify(bookCache, times(1)).deleteBook(1L);
    }

    @Test
    void shouldThrowExceptionWhenBookNotFoundForDeletion() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.deleteBookById(1L);
        });

        assertEquals("Book with ID 1 not found.", exception.getMessage());
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldEditBookSuccessfully() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old Title");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("New Title");

        // Act
        bookService.editBook(updatedBook);

        // Assert
        verify(bookRepository, times(1)).save(existingBook);
        assertEquals("New Title", existingBook.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenEditingNonexistentBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Book book = new Book();
        book.setId(1L);
        book.setTitle("New Title");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.editBook(book);
        });

        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void shouldRetrieveBooksByUser() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        Book book = new Book("Title", "Author", "Description", user, LocalDate.now(), false, 100.0);

        when(bookCache.getBooks()).thenReturn(List.of(book));

        // Act
        List<BookDto> books = bookService.getBooksByUser("test@example.com");

        // Assert
        assertEquals(1, books.size());
        assertEquals("Title", books.get(0).getTitle());
    }

    @Test
    void shouldMarkBookAsForSale() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setForSale(false);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        bookService.markAsForSale(1L);

        // Assert
        assertTrue(book.isForSale());
        verify(bookRepository, times(1)).save(book);
        verify(bookCache, times(1)).updateBooks(anyList());
    }

    @Test
    void shouldBuyBookSuccessfully() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setForSale(true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        bookService.buyBook(1L);

        // Assert
        assertFalse(book.isForSale());
        verify(bookRepository, times(1)).save(book);
        verify(bookCache, times(1)).updateBooks(anyList());
    }

    @Test
    void shouldMarkBookForSale() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setForSale(false);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        bookService.markBookForSale(1L, true);

        // Assert
        assertTrue(book.isForSale());
        verify(bookRepository, times(1)).save(book);
        verify(bookCache, times(1)).updateBooks(anyList());
    }

    @Test
    void shouldMarkBookAsSold() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setForSale(true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        bookService.markAsSold(1L);

        // Assert
        assertFalse(book.isForSale());
        verify(bookRepository, times(1)).save(book);
        verify(bookCache, times(1)).updateBooks(anyList());
    }

    @Test
    void shouldFindAllBooks() {
        // Arrange
        List<Book> books = new ArrayList<>();
        books.add(new Book("Title1", "Author1", "Description1", new User(), LocalDate.now(), false, 100.0));
        books.add(new Book("Title2", "Author2", "Description2", new User(), LocalDate.now(), true, 150.0));

        when(bookRepository.findAll()).thenReturn(books);

        // Act
        List<BookDto> result = bookService.findAllBooks();

        // Assert
        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findAll();
    }
}
