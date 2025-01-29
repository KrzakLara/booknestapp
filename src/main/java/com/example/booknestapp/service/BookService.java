package com.example.booknestapp.service;

import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.entity.Book;

import java.util.List;

public interface BookService {

    boolean createBook(String title, String author, String description, String creatorEmail, double price);

    List<BookDto> getAllBooks();

    Book getBookById(Long id);

    void deleteBookById(Long id);

    void editBook(Book book);

    List<BookDto> getBooksByUser(String email);

    void markAsForSale(Long id);

    void buyBook(Long id);

    void markBookForSale(Long id, boolean forSale);

    void markAsSold(Long id);

    List<BookDto> findAllBooks();

}
