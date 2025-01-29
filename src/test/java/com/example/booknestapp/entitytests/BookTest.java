package com.example.booknestapp.entitytests;

import com.example.booknestapp.entity.Book;
import com.example.booknestapp.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testBookBuilder() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        LocalDate createdAt = LocalDate.of(2023, 1, 1);

        Book book = Book.builder()
                .title("Test Book")
                .author("Author Name")
                .description("This is a test description")
                .createdBy(user)
                .createdAt(createdAt)
                .forSale(true)
                .price(19.99)
                .build();

        assertEquals("Test Book", book.getTitle());
        assertEquals("Author Name", book.getAuthor());
        assertEquals("This is a test description", book.getDescription());
        assertEquals(user, book.getCreatedBy());
        assertEquals(createdAt, book.getCreatedAt());
        assertTrue(book.isForSale());
        assertEquals(19.99, book.getPrice());
    }

    @Test
    void testDefaultCreatedAtWhenNull() {
        User user = new User();
        user.setId(1L);

        Book book = Book.builder()
                .title("Test Book")
                .author("Author Name")
                .description("This is a test description")
                .createdBy(user)
                .createdAt(null)
                .forSale(false)
                .price(15.50)
                .build();

        assertNotNull(book.getCreatedAt(), "CreatedAt should not be null");
        assertEquals(LocalDate.now(ZoneId.of("UTC")), book.getCreatedAt());
    }

    @Test
    void testBookSettersAndGetters() {
        User user = new User();
        user.setId(1L);

        Book book = new Book();

        book.setId(1L);
        book.setTitle("New Title");
        book.setAuthor("New Author");
        book.setDescription("New Description");
        book.setCreatedBy(user);
        book.setCreatedAt(LocalDate.of(2022, 12, 25));
        book.setForSale(true);
        book.setPrice(25.99);

        assertEquals(1L, book.getId());
        assertEquals("New Title", book.getTitle());
        assertEquals("New Author", book.getAuthor());
        assertEquals("New Description", book.getDescription());
        assertEquals(user, book.getCreatedBy());
        assertEquals(LocalDate.of(2022, 12, 25), book.getCreatedAt());
        assertTrue(book.isForSale());
        assertEquals(25.99, book.getPrice());
    }

    @Test
    void testBookEqualityAndHashCode() {
        User user = new User();
        user.setId(1L);

        Book book1 = Book.builder()
                .title("Book Title")
                .author("Author")
                .description("Description")
                .createdBy(user)
                .createdAt(LocalDate.now())
                .forSale(false)
                .price(10.00)
                .build();

        Book book2 = Book.builder()
                .title("Book Title")
                .author("Author")
                .description("Description")
                .createdBy(user)
                .createdAt(LocalDate.now())
                .forSale(false)
                .price(10.00)
                .build();

        assertEquals(book1, book2, "Books with the same data should be equal");
        assertEquals(book1.hashCode(), book2.hashCode(), "Hash codes should match for equal books");
    }

    @Test
    void testBookToString() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");

        Book book = Book.builder()
                .title("ToString Test")
                .author("Author")
                .description("Testing toString method")
                .createdBy(user)
                .createdAt(LocalDate.now())
                .forSale(false)
                .price(20.00)
                .build();

        String toString = book.toString();
        assertTrue(toString.contains("ToString Test"));
        assertTrue(toString.contains("Author"));
        assertTrue(toString.contains("Testing toString method"));
    }

    @Test
    void testDefaultConstructor() {
        Book book = new Book();

        assertNull(book.getId());
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertNull(book.getDescription());
        assertNull(book.getCreatedBy());
        assertNull(book.getCreatedAt());
        assertFalse(book.isForSale());
        assertEquals(0.0, book.getPrice());
    }

    @Test
    void testHashCodeAndEqualsConsistency() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        Book book1 = Book.builder()
                .title("Title")
                .author("Author")
                .description("Description")
                .createdBy(user1)
                .createdAt(LocalDate.now())
                .forSale(false)
                .price(15.0)
                .build();

        Book book2 = Book.builder()
                .title("Title")
                .author("Author")
                .description("Description")
                .createdBy(user1)
                .createdAt(LocalDate.now())
                .forSale(false)
                .price(15.0)
                .build();

        Book book3 = Book.builder()
                .title("Title Different")
                .author("Author Different")
                .description("Different Description")
                .createdBy(user2)
                .createdAt(LocalDate.now())
                .forSale(true)
                .price(25.0)
                .build();

        assertEquals(book1, book2, "Books with same data should be equal");
        assertNotEquals(book1, book3, "Books with different data should not be equal");
        assertNotEquals(book2, book3, "Books with different data should not be equal");
        assertEquals(book1.hashCode(), book2.hashCode(), "Hash codes for equal books should match");
        assertNotEquals(book1.hashCode(), book3.hashCode(), "Hash codes for non-equal books should not match");
    }

    @Test
    void testEqualsAndHashCodeWithDifferentFields() {
        User user = new User();
        user.setId(1L);

        Book book1 = Book.builder()
                .title("Title")
                .author("Author")
                .description("Description")
                .createdBy(user)
                .createdAt(LocalDate.now())
                .forSale(true)
                .price(20.0)
                .build();

        Book book2 = Book.builder()
                .title("Different Title")
                .author("Author")
                .description("Description")
                .createdBy(user)
                .createdAt(LocalDate.now())
                .forSale(true)
                .price(20.0)
                .build();

        assertNotEquals(book1, book2, "Books with different titles should not be equal");
        assertNotEquals(book1.hashCode(), book2.hashCode(), "Hash codes should differ for non-equal books");
    }

    @Test
    void testCreatedAtWhenNotNull() {
        User user = new User();
        user.setId(1L);

        LocalDate specificDate = LocalDate.of(2022, 5, 15);

        Book book = Book.builder()
                .title("Specific Date Book")
                .author("Author")
                .description("Description")
                .createdBy(user)
                .createdAt(specificDate)
                .forSale(true)
                .price(30.0)
                .build();

        assertEquals(specificDate, book.getCreatedAt(), "The createdAt field should match the provided value");
    }
    @Test
    void testBookBuilderMethod() {
        assertNotNull(Book.builder(), "The builder method should return a non-null instance of the Book builder.");
    }

    @Test
    void testConstructorDirectly() {
        User user = new User();
        user.setId(1L);

        LocalDate createdAt = LocalDate.of(2023, 1, 1);
        double price = 19.99;

        Book book = new Book("Test Title", "Test Author", "Test Description", user, createdAt, true, price);

        assertEquals("Test Title", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("Test Description", book.getDescription());
        assertEquals(user, book.getCreatedBy());
        assertEquals(createdAt, book.getCreatedAt());
        assertTrue(book.isForSale());
        assertEquals(price, book.getPrice());
    }

    @Test
    void testBuilderMethodCoverage() {
        // Direct call to the builder to ensure the static method is covered
        assertNotNull(Book.builder(), "The builder method should return a non-null instance of the Book builder.");
    }


}
