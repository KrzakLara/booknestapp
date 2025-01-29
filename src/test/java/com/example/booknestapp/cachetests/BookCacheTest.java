package com.example.booknestapp.cachetests;

import com.example.booknestapp.cache.BookCache;
import com.example.booknestapp.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookCacheTest {

    private BookCache bookCache;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        bookCache = new BookCache();
        book1 = new Book("Title1", "Author1", "Description1", null, LocalDate.now(), false, 10.0);
        book1.setId(1L);
        book2 = new Book("Title2", "Author2", "Description2", null, LocalDate.now(), false, 20.0);
        book2.setId(2L);
    }

    @Test
    void testAddBook() {
        bookCache.addBook(book1);
        bookCache.addBook(book2);

        List<Book> books = bookCache.getBooks();
        assertEquals(2, books.size());
        assertTrue(books.contains(book1));
        assertTrue(books.contains(book2));
    }

    @Test
    void testUpdateBooks() {
        bookCache.updateBooks(List.of(book1, book2));

        List<Book> books = bookCache.getBooks();
        assertEquals(2, books.size());
        assertTrue(books.contains(book1));
        assertTrue(books.contains(book2));
    }

    @Test
    void testUpdateBook() {
        bookCache.addBook(book1);
        Book updatedBook = new Book("Updated Title", "Updated Author", "Updated Description", null, LocalDate.now(), true, 15.0);
        updatedBook.setId(1L);

        bookCache.updateBook(updatedBook);

        List<Book> books = bookCache.getBooks();
        assertEquals(1, books.size());
        assertEquals("Updated Title", books.get(0).getTitle());
        assertEquals("Updated Author", books.get(0).getAuthor());
        assertEquals(15.0, books.get(0).getPrice());
    }

    @Test
    void testDeleteBook() {
        bookCache.addBook(book1);
        bookCache.addBook(book2);

        bookCache.deleteBook(1L);

        List<Book> books = bookCache.getBooks();
        assertEquals(1, books.size());
        assertFalse(books.contains(book1));
        assertTrue(books.contains(book2));
    }

    @Test
    void testGetBooks() {
        bookCache.addBook(book1);
        bookCache.addBook(book2);

        List<Book> books = bookCache.getBooks();
        assertEquals(2, books.size());
        assertNotSame(books, bookCache.getBooks()); // Ensure a copy is returned
    }
}
