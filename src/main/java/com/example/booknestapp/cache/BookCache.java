package com.example.booknestapp.cache;

import com.example.booknestapp.entity.Book;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookCache {
    private final List<Book> books = new ArrayList<>();

    public synchronized List<Book> getBooks() {
        return new ArrayList<>(books); // Return a copy to avoid modification from outside
    }

    public synchronized void addBook(Book book) {
        books.add(book);
    }

    public synchronized void updateBooks(List<Book> newBooks) {
        books.clear();
        books.addAll(newBooks);
    }

    public synchronized void updateBook(Book updatedBook) {
        books.removeIf(book -> book.getId().equals(updatedBook.getId()));
        books.add(updatedBook);
    }

    public synchronized void deleteBook(Long bookId) {
        books.removeIf(book -> book.getId().equals(bookId));
    }
}
