package com.example.booknestapp.cachetests;

import com.example.booknestapp.cache.BookCache;
import com.example.booknestapp.cache.CacheInitializer;
import com.example.booknestapp.entity.Book;
import com.example.booknestapp.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

class CacheInitializerTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCache bookCache;

    private CacheInitializer cacheInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheInitializer = new CacheInitializer(bookRepository, bookCache);
    }

    @Test
    void testInitializeCache() {
        Book book1 = new Book("Title1", "Author1", "Description1", null, LocalDate.now(), false, 10.0);
        book1.setId(1L);
        Book book2 = new Book("Title2", "Author2", "Description2", null, LocalDate.now(), false, 20.0);
        book2.setId(2L);

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        cacheInitializer.initializeCache();

        verify(bookRepository, times(1)).findAll();
        verify(bookCache, times(1)).updateBooks(List.of(book1, book2));
    }
}
