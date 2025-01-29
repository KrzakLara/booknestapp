package com.example.booknestapp.repolayertests;

import com.example.booknestapp.entity.Book;
import com.example.booknestapp.entity.User;
import com.example.booknestapp.model.Role;
import com.example.booknestapp.repository.BookRepository;
import com.example.booknestapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // Manually create and set all fields in the User entity to ensure consistency
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setUsername("jdoe"); // Explicitly set username to avoid relying on resolveUsername()

        // Save the user to the database
        user = userRepository.save(user);

        // Create and save Book objects associated with the user
        Book book1 = new Book("Book 1", "Author 1", "Description 1", user, LocalDate.now(), false, 10.0);
        Book book2 = new Book("Book 2", "Author 2", "Description 2", user, LocalDate.now(), true, 20.0);

        bookRepository.save(book1);
        bookRepository.save(book2);
    }

    @Test
    void testFindByCreatedBy_Id() {
        // Query books created by the user using their ID
        List<Book> books = bookRepository.findByCreatedBy_Id(user.getId());

        assertNotNull(books);
        assertEquals(2, books.size());
        assertEquals("Book 1", books.get(0).getTitle());
    }

    @Test
    void testFindByCreatedByEmail() {
        // Query books created by the user using their email
        List<Book> books = bookRepository.findByCreatedByEmail(user.getEmail());

        assertNotNull(books);
        assertEquals(2, books.size());
        assertEquals("Author 2", books.get(1).getAuthor());
    }
}
