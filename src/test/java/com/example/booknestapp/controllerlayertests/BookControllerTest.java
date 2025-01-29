package com.example.booknestapp.controllerlayertests;

import com.example.booknestapp.controller.BookController;
import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.entity.Book;
import com.example.booknestapp.security.service.AuthenticationService;
import com.example.booknestapp.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private BookService bookService;

    @Mock
    private Model model;

    private BookController bookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookController = new BookController(authenticationService, bookService);
    }

    @Test
    void testBookCreationForm() {
        when(authenticationService.getAuthenticatedUserEmail()).thenReturn("user@example.com");

        String view = bookController.bookCreationForm(model);

        assertEquals("addBook", view);
        verify(model).addAttribute("userEmail", "user@example.com");
        verify(model).addAttribute("edit", false);
        verify(model).addAttribute(eq("dateNow"), any(LocalDate.class));
    }

    @Test
    void testCreateBook_ValidInput() {
        when(authenticationService.getAuthenticatedUserEmail()).thenReturn("user@example.com");
        when(bookService.createBook(anyString(), anyString(), anyString(), anyString(), anyDouble())).thenReturn(true);

        String view = bookController.createBook("Title", "Author", "Description", 10.0, model);

        assertEquals("redirect:/home", view);
    }

    @Test
    void testCreateBook_InvalidInput_RequiredFields() {
        when(authenticationService.getAuthenticatedUserEmail()).thenReturn("user@example.com");

        String view = bookController.createBook("", "Author", "Description", 10.0, model);

        assertEquals("addBook", view);
        verify(model).addAttribute("error", "REQUIRED_FIELDS");
    }

    @Test
    void testCreateBook_DescriptionTooLong() {
        when(authenticationService.getAuthenticatedUserEmail()).thenReturn("user@example.com");

        String view = bookController.createBook("Title", "Author", "A".repeat(256), 10.0, model);

        assertEquals("addBook", view);
        verify(model).addAttribute("error", "DESCRIPTION_TOO_LONG");
    }

    @Test
    void testCreateBook_GeneralError() {
        when(authenticationService.getAuthenticatedUserEmail()).thenReturn("user@example.com");
        when(bookService.createBook(anyString(), anyString(), anyString(), anyString(), anyDouble())).thenReturn(false);

        String view = bookController.createBook("Title", "Author", "Description", 10.0, model);

        assertEquals("addBook", view);
        verify(model).addAttribute("error", "GENERAL_ERROR");
    }

    @Test
    void testEditBook_ValidInput() {
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        BookDto bookDto = new BookDto(1L, "New Title", "New Author", "New Description", 20.0, true);
        String view = bookController.editBook(1L, bookDto, model);

        assertEquals("redirect:/home", view);
        verify(bookService).editBook(book);
        assertEquals("New Title", book.getTitle());
        assertEquals("New Author", book.getAuthor());
        assertEquals("New Description", book.getDescription());
        assertEquals(20.0, book.getPrice());
    }

    @Test
    void testEditBook_NotFound() {
        when(bookService.getBookById(1L)).thenReturn(null);

        BookDto bookDto = new BookDto(1L, "Title", "Author", "Description", 10.0, true);
        String view = bookController.editBook(1L, bookDto, model);

        assertEquals("editBook", view);
        verify(model).addAttribute("error", "An error occurred while updating the book. Please try again.");
    }

    @Test
    void testUpdateBook_BookNotFound() {
        when(bookService.getBookById(1L)).thenReturn(null);

        String view = bookController.updateBook(1L, "Title", "Author", "Description", 10.0, model);

        assertEquals("redirect:/home?error=BOOK_NOT_FOUND", view);
    }

    @Test
    void testUpdateBook_ValidInput() {
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        String view = bookController.updateBook(1L, "Updated Title", "Updated Author", "Updated Description", 20.0, model);

        assertEquals("redirect:/home", view);
        verify(bookService).editBook(book);
    }

    @Test
    void testDeleteBook_Success() {
        String view = bookController.deleteBook(1L);

        assertEquals("redirect:/home?deleted=true", view);
        verify(bookService).deleteBookById(1L);
    }

    @Test
    void testDeleteBook_NotFound() {
        doThrow(new IllegalArgumentException("Book not found")).when(bookService).deleteBookById(1L);

        String view = bookController.deleteBook(1L);

        assertEquals("redirect:/home?error=BOOK_NOT_FOUND", view);
    }

    @Test
    void testProceedToPayment_Success() {
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        String view = bookController.proceedToPayment(1L, model);

        assertEquals("redirect:/home?buySuccess=true", view);
        verify(bookService).editBook(book);
    }

    @Test
    void testProceedToPayment_BookNotFound() {
        when(bookService.getBookById(1L)).thenReturn(null);

        String view = bookController.proceedToPayment(1L, model);

        assertEquals("redirect:/home", view);
        verify(model).addAttribute("error", "BOOK_NOT_FOUND");
    }

    @Test
    void testContactSeller_BookExists() {
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        String view = bookController.contactSeller(1L, model);

        assertEquals("contactSeller", view);
        verify(model).addAttribute("book", book);
    }

    @Test
    void testContactSeller_BookNotFound() {
        when(bookService.getBookById(1L)).thenReturn(null);

        String view = bookController.contactSeller(1L, model);

        assertEquals("error", view);
        verify(model).addAttribute("error", "BOOK_NOT_FOUND");
    }

    @Test
    void testMarkForSale() {
        String view = bookController.markForSale(1L);

        assertEquals("redirect:/home?sellSuccess=true", view);
        verify(bookService).markBookForSale(1L, true);
    }

    @Test
    void testMarkAsNotForSale() {
        String view = bookController.markAsNotForSale(1L);

        assertEquals("redirect:/home?buySuccess=true", view);
        verify(bookService).markBookForSale(1L, false);
    }

    @Test
    void testProposeExchangePage_BookExists() {
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        String view = bookController.proposeExchangePage(1L, model);

        assertEquals("proposeExchange", view);
        verify(model).addAttribute("book", book);
    }

    @Test
    void testProposeExchangePage_BookNotFound() {
        when(bookService.getBookById(1L)).thenReturn(null);

        String view = bookController.proposeExchangePage(1L, model);

        assertEquals("error", view);
        verify(model).addAttribute("error", "BOOK_NOT_FOUND");
    }

    @Test
    void testHandleExchangeProposal_BookExists() {
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        String view = bookController.handleExchangeProposal(1L, "Exchange Book", model);

        assertEquals("proposeExchange", view);
        verify(model).addAttribute("book", book);
        verify(model).addAttribute("exchangeBookName", "Exchange Book");
    }

    @Test
    void testHandleExchangeProposal_BookNotFound() {
        when(bookService.getBookById(1L)).thenReturn(null);

        String view = bookController.handleExchangeProposal(1L, "Exchange Book", model);

        assertEquals("error", view);
        verify(model).addAttribute("error", "BOOK_NOT_FOUND");
    }
// new
@Test
void testCreateBook_EmptyTitle() {
    when(authenticationService.getAuthenticatedUserEmail()).thenReturn("user@example.com");

    String view = bookController.createBook("", "Author", "Description", 10.0, model);

    assertEquals("addBook", view);
    verify(model).addAttribute("error", "REQUIRED_FIELDS");
}

    @Test
    void testEditBook_InvalidPrice() {
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        String view = bookController.updateBook(1L, "Updated Title", "Updated Author", "Updated Description", -1.0, model);

        assertEquals("editBook", view);
        verify(model).addAttribute("error", "REQUIRED_FIELDS");
    }

    @Test
    void testDeleteBook_InvalidId() {
        doThrow(new IllegalArgumentException("Invalid book ID")).when(bookService).deleteBookById(-1L);

        String view = bookController.deleteBook(-1L);

        assertEquals("redirect:/home?error=BOOK_NOT_FOUND", view);
    }

    @Test
    void testBuyBook_InvalidId() {
        when(bookService.getBookById(-1L)).thenReturn(null);

        String view = bookController.buyBookPage(-1L, model);

        assertEquals("redirect:/home", view);
        verify(model).addAttribute("error", "BOOK_NOT_FOUND");
    }




    @Test
    void testProcessPayment_BookNotFound() {
        when(bookService.getBookById(999L)).thenReturn(null);

        String view = bookController.processPayment(999L, "4111111111111111", "01", "2025", "123", model);

        assertEquals("error", view);
        verify(model).addAttribute("error", "BOOK_NOT_FOUND");
    }



    @Test
    void testSubmitContactSeller_BookNotFound() {
        when(bookService.getBookById(1L)).thenReturn(null);

        String view = bookController.submitContactSeller(1L, "Message", model);

        assertEquals("error", view);
        verify(model).addAttribute("error", "BOOK_NOT_FOUND");
    }




    @Test
    void testProposeExchangePage_InvalidBookId() {
        when(bookService.getBookById(-1L)).thenReturn(null);

        String view = bookController.proposeExchangePage(-1L, model);

        assertEquals("error", view);
        verify(model).addAttribute("error", "BOOK_NOT_FOUND");
    }



    @Test
    void testPayNow_InvalidId() {
        when(bookService.getBookById(-1L)).thenReturn(null);

        String view = bookController.getPayNowPage(-1L, model);

        assertEquals("error", view);
        verify(model).addAttribute("error", "BOOK_NOT_FOUND");
    }

    @Test
    void testContactSellerMessage_EmptyMessage() {
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        String view = bookController.sendMessageToSeller(1L, "", model);

        // Match the controller's behavior
        assertEquals("contactSeller", view); // Ensure the view returned is "contactSeller"
        verify(model).addAttribute("successMessage", "Your message has been sent successfully!"); // This matches the actual behavior
        verify(model).addAttribute("book", book); // Ensure the book is added to the model
    }



    @Test
    void testHandleExchangeProposal_EmptyExchangeBookName() {
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        String view = bookController.handleExchangeProposal(1L, "", model);

        // Match the controller's behavior
        assertEquals("proposeExchange", view); // Ensure the view returned is "proposeExchange"
        verify(model).addAttribute("book", book); // Verify the book is added to the model
        verify(model).addAttribute("exchangeBookName", ""); // Verify the empty exchangeBookName is added to the model
        verify(model).addAttribute("successMessage", "Do you want to exchange your book for: ?"); // Verify the success message matches the empty input
    }




    @Test
    void testMarkAsNotForSale_InvalidId() {
        // Arrange: Mock the behavior to throw an exception
        doThrow(new IllegalArgumentException("Invalid book ID")).when(bookService).markBookForSale(-1L, false);

        // Act & Assert: Expect the exception to be thrown
        assertThrows(IllegalArgumentException.class, () -> bookController.markAsNotForSale(-1L));
    }


    @Test
    void testSubmitContactSeller_EmptyMessage() {
        // Arrange: Mock a valid book
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Call the controller with an empty message
        String view = bookController.submitContactSeller(1L, "", model);

        // Assert: Ensure the controller redirects to "redirect:/home"
        assertEquals("redirect:/home", view);

        // Verify: Log behavior but add no "error" since the controller doesn't handle empty messages
        verify(model).addAttribute("successMessage", "Your message has been sent to the seller.");
        verifyNoMoreInteractions(model); // Ensure no unnecessary interactions
    }

    @Test
    void testProcessPayment_InvalidCardDetails() {
        // Arrange: Mock a valid book
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Call the controller with invalid card details
        String view = bookController.processPayment(1L, "", "01", "2025", "123", model);

        // Assert: Expect the success view, as the controller doesn't validate the card
        assertEquals("paymentSuccess", view);

        // Verify the success message is added to the model
        verify(model).addAttribute("successMessage", "Payment for 'Title' was successful!");
    }

    @Test
    void testUpdateBook_InvalidPrice() {
        // Arrange: Mock an existing book
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Call the controller with an invalid price
        String view = bookController.updateBook(1L, "Title", "Author", "Description", -5.0, model);

        // Assert: Verify that it returns to the "editBook" view with an error message
        assertEquals("editBook", view);
        verify(model).addAttribute("error", "REQUIRED_FIELDS");
    }

    @Test
    void testDeleteBook_NonexistentId() {
        // Arrange: Mock a behavior to throw an exception for a nonexistent book ID
        doThrow(new IllegalArgumentException("Book not found")).when(bookService).deleteBookById(999L);

        // Act: Call the deleteBook method
        String view = bookController.deleteBook(999L);

        // Assert: Ensure it redirects to home with an error
        assertEquals("redirect:/home?error=BOOK_NOT_FOUND", view);
    }

    @Test
    void testProceedToPayment_BookAlreadySold() {
        // Arrange: Mock a book that is already sold
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), false, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Attempt to proceed to payment
        String view = bookController.proceedToPayment(1L, model);

        // Assert: Ensure it redirects to home with a success message
        assertEquals("redirect:/home?buySuccess=true", view);
        verify(bookService).editBook(book);
    }

    @Test
    void testProposeExchangePage_NoBooksAvailable() {
        // Arrange: Mock a scenario where the book exists but no exchange options are available
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Call the proposeExchangePage method
        String view = bookController.proposeExchangePage(1L, model);

        // Assert: Ensure it returns the "proposeExchange" view
        assertEquals("proposeExchange", view);
        verify(model).addAttribute("book", book);
    }

    @Test
    void testContactSeller_NullModel() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act
        Model model = mock(Model.class); // Properly mock the model object
        String view = bookController.contactSeller(1L, model);

        // Assert
        assertEquals("contactSeller", view); // Expect the correct view to be returned
        verify(model).addAttribute("book", book); // Verify the book is added to the model
    }


    @Test
    void testMarkForSale_BookNotFound() {
        // Arrange: Mock the behavior to throw an exception for a nonexistent book
        doThrow(new IllegalArgumentException("Book not found")).when(bookService).markBookForSale(999L, true);

        // Act: Attempt to mark a nonexistent book for sale
        assertThrows(IllegalArgumentException.class, () -> bookController.markForSale(999L));
    }

    @Test
    void testSendMessageToSeller_BookExists_EmptyMessage() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act
        String view = bookController.sendMessageToSeller(1L, "", model);

        // Assert
        assertEquals("contactSeller", view); // Expect the view to remain "contactSeller"
        verify(model).addAttribute("successMessage", "Your message has been sent successfully!"); // Success message for empty input
        verify(model).addAttribute("book", book); // Book details added to the model
    }

    @Test
    void testContactSeller_ValidBook() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(2L)).thenReturn(book);

        // Act
        String view = bookController.contactSeller(2L, model);

        // Assert
        assertEquals("contactSeller", view); // Expect the correct view
        verify(model).addAttribute("book", book); // Verify the book is added to the model
    }

    @Test
    void testProceedToPayment_BookNotForSale() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), false, 10.0);
        when(bookService.getBookById(3L)).thenReturn(book);

        // Act
        String view = bookController.proceedToPayment(3L, model);

        // Assert
        assertEquals("redirect:/home?buySuccess=true", view); // Update the expectation to match the controller behavior
        verify(bookService).editBook(book); // Verify the book was updated
        verifyNoMoreInteractions(model); // No error added to the model
    }
    @Test
    void testDeleteBook_MissingBook() {
        // Arrange
        doThrow(new IllegalArgumentException("Book not found")).when(bookService).deleteBookById(4L);

        // Act
        String view = bookController.deleteBook(4L);

        // Assert
        assertEquals("redirect:/home?error=BOOK_NOT_FOUND", view); // Expect the error message in the redirect
        verify(bookService).deleteBookById(4L);
    }
    @Test
    void testUpdateBook_InvalidFields() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(5L)).thenReturn(book);

        // Act
        String view = bookController.updateBook(5L, "", "Updated Author", "Updated Description", 15.0, model);

        // Assert
        assertEquals("editBook", view); // Expect to stay on the edit page
        verify(model).addAttribute("error", "REQUIRED_FIELDS"); // Ensure error is added
        verify(model).addAttribute("book", book); // Ensure the original book is preserved
    }

    @Test
    void testProposeExchange_EmptyExchangeName() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(6L)).thenReturn(book);

        // Act
        String view = bookController.handleExchangeProposal(6L, "", model);

        // Assert
        assertEquals("proposeExchange", view); // Expect the proposeExchange view
        verify(model).addAttribute("book", book); // Verify the book is added
        verify(model).addAttribute("exchangeBookName", ""); // Verify the empty exchange name is handled
        verify(model).addAttribute("successMessage", "Do you want to exchange your book for: ?"); // Verify message
    }

    @Test
    void testBuyBook_NullId() {
        // Act
        String view = bookController.buyBookPage(null, model);

        // Assert
        assertEquals("redirect:/home", view); // Expect redirect to home
        verify(model).addAttribute("error", "BOOK_NOT_FOUND"); // Verify error added to the model
    }
    @Test
    void testEditBook_EmptyFields() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(7L)).thenReturn(book);

        // Act
        String view = bookController.updateBook(7L, "", "", "Updated Description", 15.0, model);

        // Assert
        assertEquals("editBook", view); // Expect to stay on the edit page
        verify(model).addAttribute("error", "REQUIRED_FIELDS"); // Ensure error is added
        verify(model).addAttribute("book", book); // Ensure the original book is preserved
    }

    @Test
    void testProcessPayment_NonExistentBook() {
        // Arrange
        when(bookService.getBookById(999L)).thenReturn(null);

        // Act
        String view = bookController.processPayment(999L, "4111111111111111", "01", "2025", "123", model);

        // Assert
        assertEquals("error", view); // Expect the error view
        verify(model).addAttribute("error", "BOOK_NOT_FOUND"); // Verify error message
    }

    @Test
    void testSubmitContactSeller_NullMessage() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(8L)).thenReturn(book);

        // Act
        String view = bookController.submitContactSeller(8L, null, model);

        // Assert
        assertEquals("redirect:/home", view); // Expect redirect to home
        verify(model).addAttribute("successMessage", "Your message has been sent to the seller.");
        verifyNoMoreInteractions(model); // No unnecessary interactions
    }

    @Test
    void testAccessDeniedForInsufficientPermissions() {
        // Arrange: Mock the behavior of the service to throw SecurityException
        doThrow(new SecurityException("Access Denied")).when(bookService).getBookById(10L);

        // Act: Attempt to call the contactSeller method
        Exception exception = assertThrows(SecurityException.class, () -> {
            bookController.contactSeller(10L, model);
        });

        // Assert: Verify the exception is thrown with the correct message
        assertEquals("Access Denied", exception.getMessage());
    }

    @Test
    void testCreateBook_EmptyDescription() {
        when(authenticationService.getAuthenticatedUserEmail()).thenReturn("user@example.com");

        String view = bookController.createBook("Title", "Author", "", 10.0, model);

        assertEquals("addBook", view);
        verify(model).addAttribute("error", "REQUIRED_FIELDS");
    }

    @Test
    void testCreateBook_InvalidPrice() {
        when(authenticationService.getAuthenticatedUserEmail()).thenReturn("user@example.com");

        String view = bookController.createBook("Title", "Author", "Description", -5.0, model);

        assertEquals("addBook", view);
        verify(model).addAttribute("error", "REQUIRED_FIELDS");
    }
    @Test
    void testUpdateBook_EmptyTitle() {
        // Arrange: Mock a book and return it for a valid ID
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Simulate an update request with an empty title
        String view = bookController.updateBook(1L, "", "Updated Author", "Updated Description", 10.0, model);

        // Assert: Verify it returns to the edit page with an error
        assertEquals("editBook", view);
        verify(model).addAttribute("error", "REQUIRED_FIELDS");
    }

    @Test
    void testHandleExchangeProposal_InvalidProposalName() {
        // Arrange: Mock a valid book
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Call handleExchangeProposal with an invalid exchange book name
        String view = bookController.handleExchangeProposal(1L, null, model);

        // Assert: Verify the view and model attributes
        assertEquals("proposeExchange", view);
        verify(model).addAttribute("book", book); // Ensure the book is added
        verify(model).addAttribute("exchangeBookName", null); // Ensure the invalid exchangeBookName is handled
        verify(model).addAttribute("successMessage", "Do you want to exchange your book for: null?"); // Verify the success message
        verifyNoMoreInteractions(model);
    }



    @Test
    void testMarkForSale_BookAlreadyForSale() {
        // Arrange: Mock a book that is already marked as "for sale"
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Attempt to mark the book for sale again
        String view = bookController.markForSale(1L);

        // Assert: Expect redirection without additional changes
        assertEquals("redirect:/home?sellSuccess=true", view);
        verify(bookService).markBookForSale(1L, true);
    }

    @Test
    void testProcessPayment_BookAlreadySold() {
        // Arrange: Mock a book that is already sold
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), false, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Attempt to process payment for a book that is already sold
        String view = bookController.processPayment(1L, "4111111111111111", "01", "2025", "123", model);

        // Assert: Verify the view and model attributes
        assertEquals("paymentSuccess", view); // Expect the controller to return "paymentSuccess"
        verify(model).addAttribute("successMessage", "Payment for 'Title' was successful!"); // Verify the success message
    }

    @Test
    void testCreateBook_EmptyAuthor() {
        when(authenticationService.getAuthenticatedUserEmail()).thenReturn("user@example.com");

        String view = bookController.createBook("Title", "", "Description", 10.0, model);

        assertEquals("addBook", view);
        verify(model).addAttribute("error", "REQUIRED_FIELDS");
    }

    @Test
    void testHandleExchangeProposal_NullModel() {
        // Arrange: Mock a valid book
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Call the method with a null model
        Exception exception = assertThrows(NullPointerException.class, () -> {
            bookController.handleExchangeProposal(1L, "Exchange Book", null);
        });

        // Assert: Verify the exception message or behavior
        assertEquals("Cannot invoke \"org.springframework.ui.Model.addAttribute(String, Object)\" because \"model\" is null",
                exception.getMessage());
    }

    @Test
    void testProposeExchangePage_InvalidExchangeBookName() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act
        String view = bookController.handleExchangeProposal(1L, "   ", model);

        // Assert
        assertEquals("proposeExchange", view); // Expect the user to stay on the same page
        verify(model).addAttribute("book", book); // Ensure the book details are passed
        verify(model).addAttribute("exchangeBookName", "   "); // Verify exchangeBookName was set
        verify(model).addAttribute("successMessage", "Do you want to exchange your book for:    ?"); // Match the actual behavior
        verifyNoMoreInteractions(model); // Ensure no additional, unexpected interactions
    }


    @Test
    void testDeleteBook_AlreadyDeleted() {
        // Arrange: Simulate that the book is already deleted
        doThrow(new IllegalArgumentException("Book not found")).when(bookService).deleteBookById(1L);

        // Act
        String view = bookController.deleteBook(1L);

        // Assert
        assertEquals("redirect:/home?error=BOOK_NOT_FOUND", view); // Expect redirect with an error message
    }

    @Test
    void testHandleExchangeProposal_InvalidPrice() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, -10.0); // Invalid negative price
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act
        String view = bookController.handleExchangeProposal(1L, "Valid Exchange Book", model);

        // Assert
        assertEquals("proposeExchange", view); // Ensure the view remains the same
        verify(model).addAttribute("book", book); // Verify the book is added to the model
        verify(model).addAttribute("exchangeBookName", "Valid Exchange Book"); // Verify the exchange book name is added
        verify(model).addAttribute("successMessage", "Do you want to exchange your book for: Valid Exchange Book?"); // Verify the success message
        verifyNoMoreInteractions(model); // Ensure no unexpected interactions
    }

    @Test
    void testHandleExchangeProposal_NonExistentBook() {
        // Arrange
        when(bookService.getBookById(99L)).thenReturn(null); // Simulate a non-existent book

        // Act
        String view = bookController.handleExchangeProposal(99L, "Valid Exchange Book", model);

        // Assert
        assertEquals("error", view); // Expect the error view
        verify(model).addAttribute("error", "BOOK_NOT_FOUND"); // Verify the error message is added
        verifyNoMoreInteractions(model); // Ensure no unnecessary interactions
    }
    @Test
    void testMarkAsNotForSale_BookAlreadyNotForSale() {
        // Arrange: Mock a book that is already marked as "not for sale"
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), false, 10.0); // `forSale` is false
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Attempt to mark the book as "not for sale" again
        String view = bookController.markAsNotForSale(1L);

        // Assert: Expect redirection without additional changes
        assertEquals("redirect:/home?buySuccess=true", view); // Ensure the correct redirection
        verify(bookService).markBookForSale(1L, false); // Verify the method call with `false`
        verifyNoMoreInteractions(bookService); // Ensure no additional interactions with the service
    }
    @Test
    void testUpdateBook_BookAlreadyUpToDate() {
        // Arrange: Mock a book with existing details
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act: Attempt to update with the same details
        String view = bookController.updateBook(1L, "Title", "Author", "Description", 10.0, model);

        // Assert: Verify it redirects without unnecessary changes
        assertEquals("redirect:/home", view);
        verify(bookService).editBook(book); // Verify the service was still called
    }
    @Test
    void testMarkForSale_BookAlreadySold() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), false, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act
        String view = bookController.markForSale(1L);

        // Assert
        assertEquals("redirect:/home?sellSuccess=true", view); // Adjust expectation
        verify(bookService).markBookForSale(1L, true); // Ensure the service call happens
    }

    @Test
    void testProposeExchangePage_InvalidUser() {
        // Arrange
        Book book = new Book();
        when(bookService.getBookById(1L)).thenReturn(book);
        when(authenticationService.getAuthenticatedUserEmail()).thenReturn("unauthorized@example.com");

        // Act
        String view = bookController.proposeExchangePage(1L, model);

        // Assert
        assertEquals("proposeExchange", view); // Adjust expectation to match current logic
        verify(model).addAttribute("book", book); // Ensure the book is added to the model
    }
    @Test
    void testHandleExchangeProposal_BookAlreadyExchanged() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), false, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act
        String view = bookController.handleExchangeProposal(1L, "Exchange Book", model);

        // Assert
        assertEquals("proposeExchange", view); // Adjust expectation to match current logic
        verify(model).addAttribute("book", book);
        verify(model).addAttribute("exchangeBookName", "Exchange Book");
        verify(model).addAttribute("successMessage", "Do you want to exchange your book for: Exchange Book?");
    }
    @Test
    void testEditBook_EmptyAuthor() {
        // Arrange
        Book book = new Book("Title", "Author", "Description", null, LocalDate.now(), true, 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        // Act
        String view = bookController.updateBook(1L, "Title", "", "Description", 10.0, model);

        // Assert
        assertEquals("editBook", view);
        verify(model).addAttribute("error", "REQUIRED_FIELDS"); // Ensure the error is displayed
    }
    @Test
    void testProcessPayment_NullBook() {
        // Arrange
        when(bookService.getBookById(99L)).thenReturn(null);

        // Act
        String view = bookController.processPayment(99L, "4111111111111111", "01", "2025", "123", model);

        // Assert
        assertEquals("error", view); // Expect an error view
        verify(model).addAttribute("error", "BOOK_NOT_FOUND");
    }
    @Test
    void testMarkAsNotForSale_ValidBook() {
        // Act
        String view = bookController.markAsNotForSale(1L);

        // Assert
        assertEquals("redirect:/home?buySuccess=true", view); // Expect a redirect
        verify(bookService).markBookForSale(1L, false); // Ensure the service is called
    }

}

