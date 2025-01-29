package com.example.booknestapp.entitytests;

import com.example.booknestapp.entity.Book;
import com.example.booknestapp.entity.Choice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChoiceTest {

    @Test
    void testChoiceConstructorAndGetters() {
        // Arrange
        Book book = new Book(); // Assuming a default constructor exists for Book
        book.setId(1L);
        book.setTitle("Test Book");

        String option = "Option A";
        Long votes = 10L;

        // Act
        Choice choice = new Choice(book, option, votes);

        // Assert
        assertNotNull(choice.getBook(), "Book should not be null");
        assertEquals(book, choice.getBook(), "Book should match the one passed to the constructor");
        assertEquals(option, choice.getOption(), "Option should match the one passed to the constructor");
        assertEquals(votes, choice.getVotes(), "Votes should match the one passed to the constructor");
    }

    @Test
    void testSetters() {
        // Arrange
        Choice choice = new Choice();
        Book book = new Book(); // Assuming a default constructor exists for Book
        book.setId(2L);
        book.setTitle("Another Test Book");

        String option = "Option B";
        Long votes = 20L;

        // Act
        choice.setBook(book);
        choice.setOption(option);
        choice.setVotes(votes);

        // Assert
        assertEquals(book, choice.getBook(), "Book should match the one set");
        assertEquals(option, choice.getOption(), "Option should match the one set");
        assertEquals(votes, choice.getVotes(), "Votes should match the one set");
    }
}
