package com.example.booknestapp.service;

import com.example.booknestapp.cache.BookCache;
import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.entity.Book;
import com.example.booknestapp.repository.BookRepository;
import com.example.booknestapp.security.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IBookService implements BookService {

    private final BookRepository bookRepository;
    private final UserService userService;
    private final BookCache bookCache;

    @Override
    public boolean createBook(String title, String author, String description, String creatorEmail, double price) {
        var createdBy = userService.findByEmail(creatorEmail);
        if (createdBy == null) {
            throw new IllegalArgumentException("User with email " + creatorEmail + " does not exist.");
        }

        if (description.length() > 255) {
            return false;
        }

        var book = new Book(title, author, description, createdBy, LocalDate.now(ZoneId.of("UTC")), false, price);
        bookRepository.save(book);
        bookCache.addBook(book);
        return true;
    }

    @Override
    public List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }



    @Transactional
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }


    @Override
    public void deleteBookById(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book with ID " + id + " not found.");
        }
        bookRepository.deleteById(id);
        bookCache.deleteBook(id);
    }

    @Override
    @Transactional
    public void editBook(Book book) {
        Book existingBook = bookRepository.findById(book.getId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setDescription(book.getDescription());
        existingBook.setPrice(book.getPrice());

        bookRepository.save(existingBook);


        refreshCache();
    }



    @Override
    public List<BookDto> getBooksByUser(String email) {
        return bookCache.getBooks().stream()
                .filter(book -> book.getCreatedBy().getEmail().equals(email))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    @Override
    public void markAsForSale(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        book.setForSale(true);
        bookRepository.save(book);
        refreshCache();
    }

    @Override
    public void buyBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book with ID " + id + " not found."));
        book.setForSale(false);
        bookRepository.save(book);
        refreshCache();
    }

    @Override
    public void markBookForSale(Long id, boolean forSale) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        book.setForSale(forSale);
        bookRepository.save(book);
        refreshCache();
    }

    @Override
    public void markAsSold(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book with ID " + id + " not found"));
        book.setForSale(false); // Mark as sold
        bookRepository.save(book);
        refreshCache();
    }

    @Override
    public List<BookDto> findAllBooks() {
        return getAllBooks();
    }

    private void refreshCache() {
        List<Book> updatedBooks = bookRepository.findAll();
        bookCache.updateBooks(updatedBooks);
    }

    private BookDto mapToDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getPrice(),
                book.isForSale()
        );
    }
}
