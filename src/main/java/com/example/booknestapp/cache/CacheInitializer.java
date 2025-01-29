package com.example.booknestapp.cache;

import com.example.booknestapp.cache.BookCache;
import com.example.booknestapp.entity.Book;
import com.example.booknestapp.repository.BookRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheInitializer {

    private final BookRepository bookRepository;
    private final BookCache bookCache;

    @PostConstruct
    public void initializeCache() {
        List<Book> books = bookRepository.findAll(); // Fetch all books from the database
        bookCache.updateBooks(books); // Populate the cache
        System.out.println("Book cache initialized with " + books.size() + " books.");
    }
}
