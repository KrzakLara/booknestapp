package com.example.booknestapp.controller;

import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.entity.Book;
import com.example.booknestapp.security.service.AuthenticationService;
import com.example.booknestapp.service.BookService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final AuthenticationService authenticationService;
    private final BookService bookService;

    @GetMapping("/addANewBook")
    public String bookCreationForm(Model model) {
        var userEmail = authenticationService.getAuthenticatedUserEmail();
        model.addAttribute("userEmail", userEmail);
        model.addAttribute("edit", false);
        model.addAttribute("dateNow", LocalDate.now());
        return "addBook";
    }

    @PostMapping("/private/api/poll/create")
    public String createBook(@RequestParam("title") String title,
                             @RequestParam("author") String author,
                             @RequestParam("description") String description,
                             @RequestParam("price") double price,
                             Model model) {
        var userEmail = authenticationService.getAuthenticatedUserEmail();

        if (title.isBlank() || author.isBlank() || description.isBlank() || price <= 0) {
            model.addAttribute("error", "REQUIRED_FIELDS");
            model.addAttribute("userEmail", userEmail);
            return "addBook";
        }

        if (description.length() > 255) {
            model.addAttribute("error", "DESCRIPTION_TOO_LONG");
            model.addAttribute("userEmail", userEmail);
            return "addBook";
        }

        boolean isCreated = bookService.createBook(title, author, description, userEmail, price);

        if (!isCreated) {
            model.addAttribute("error", "GENERAL_ERROR");
            return "addBook";
        }

        return "redirect:/home";
    }

    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, @ModelAttribute BookDto bookDto, Model model) {
        try {

            Book book = bookService.getBookById(id);

            if (book == null) {
                throw new IllegalArgumentException("Book not found");
            }


            book.setTitle(bookDto.getTitle());
            book.setAuthor(bookDto.getAuthor());
            book.setDescription(bookDto.getDescription());
            book.setPrice(bookDto.getPrice());


            bookService.editBook(book);


            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while updating the book. Please try again.");
            return "editBook";
        }
    }

    @PostMapping("/private/api/poll/edit/{id}")
    public String updateBook(@PathVariable Long id,
                             @RequestParam("title") String title,
                             @RequestParam("author") String author,
                             @RequestParam("description") String description,
                             @RequestParam("price") double price,
                             Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return "redirect:/home?error=BOOK_NOT_FOUND";
        }
        if (title.isBlank() || author.isBlank() || description.isBlank() || price <= 0) {
            model.addAttribute("error", "REQUIRED_FIELDS");
            model.addAttribute("book", book);
            return "editBook";
        }
        if (description.length() > 255) {
            model.addAttribute("error", "DESCRIPTION_TOO_LONG");
            model.addAttribute("book", book);
            return "editBook";
        }


        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setPrice(price);
        bookService.editBook(book);


        return "redirect:/home";
    }

    @RolesAllowed("ROLE_ADMIN")
    @Transactional
    @PostMapping("/private/api/poll/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBookById(id);
            return "redirect:/home?deleted=true";
        } catch (IllegalArgumentException e) {
            log.error("Book with ID {} not found: {}", id, e.getMessage());
            return "redirect:/home?error=BOOK_NOT_FOUND";
        }
    }

    @PostMapping("/private/api/poll/sell/{id}")
    public String markForSale(@PathVariable Long id) {
        bookService.markBookForSale(id, true);
        return "redirect:/home?sellSuccess=true";
    }

    @PostMapping("/private/api/poll/buy/{id}")
    public String markAsNotForSale(@PathVariable Long id) {
        bookService.markBookForSale(id, false);
        return "redirect:/home?buySuccess=true";
    }

    @GetMapping("/contactSeller/{id}")
    public String contactSeller(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            model.addAttribute("error", "BOOK_NOT_FOUND");
            return "error";
        }
        model.addAttribute("book", book);
        return "contactSeller";
    }

    @PostMapping("/pay/{id}")
    public String proceedToPayment(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            model.addAttribute("error", "BOOK_NOT_FOUND");
            return "redirect:/home";
        }
        book.setForSale(false); // Mark as sold
        bookService.editBook(book); // Update in the database
        return "redirect:/home?buySuccess=true";
    }

    @GetMapping("/proposeExchange/{id}")
    public String proposeExchangePage(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            model.addAttribute("error", "BOOK_NOT_FOUND");
            return "error";
        }
        model.addAttribute("book", book);
        return "proposeExchange"; // Name of the Thymeleaf template
    }

    @PostMapping("/proposeExchange/{id}")
    public String handleExchangeProposal(@PathVariable Long id,
                                         @RequestParam("exchangeBookName") String exchangeBookName,
                                         Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            model.addAttribute("error", "BOOK_NOT_FOUND");
            return "error";
        }
        // Display the confirmation message with the exchanged book name
        model.addAttribute("book", book);
        model.addAttribute("exchangeBookName", exchangeBookName);
        model.addAttribute("successMessage", "Do you want to exchange your book for: " + exchangeBookName + "?");
        return "proposeExchange";
    }


    @GetMapping("/buyBook/{id}")
    public String buyBookPage(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            model.addAttribute("error", "BOOK_NOT_FOUND");
            return "redirect:/home";
        }
        model.addAttribute("book", book);
        return "buyBook";
    }
    @PostMapping("/submitContactSeller/{id}")
    public String submitContactSeller(@PathVariable Long id, @RequestParam("message") String message, Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            model.addAttribute("error", "BOOK_NOT_FOUND");
            return "error"; // Or redirect to a custom error page
        }

        log.info("Message to seller for book '{}': {}", book.getTitle(), message);

        model.addAttribute("successMessage", "Your message has been sent to the seller.");
        return "redirect:/home"; // Redirect back to the home page with a success message
    }
    @PostMapping("/contactSellerMessage/{id}")
    public String sendMessageToSeller(@PathVariable Long id,
                                      @RequestParam("message") String message,
                                      Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            model.addAttribute("error", "BOOK_NOT_FOUND");
            return "error"; // Redirect to a generic error page if needed
        }
        // Simulate saving or sending the message
        System.out.println("Message to seller: " + message);

        model.addAttribute("successMessage", "Your message has been sent successfully!");
        model.addAttribute("book", book);
        return "contactSeller";
    }
    @PostMapping("/processPayment/{id}")
    public String processPayment(@PathVariable Long id,
                                 @RequestParam String cardNumber,
                                 @RequestParam String expiryMonth,
                                 @RequestParam String expiryYear,
                                 @RequestParam String ccv,
                                 Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            model.addAttribute("error", "BOOK_NOT_FOUND");
            return "error";
        }

        // Simulate payment processing logic
        boolean paymentSuccessful = true; // Replace with real payment logic

        if (paymentSuccessful) {
            model.addAttribute("successMessage", "Payment for '" + book.getTitle() + "' was successful!");
            return "paymentSuccess";
        } else {
            model.addAttribute("error", "Payment failed. Please try again.");
            return "payNow";
        }
    }


    @GetMapping("/payNow/{id}")
    public String getPayNowPage(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            model.addAttribute("error", "BOOK_NOT_FOUND");
            return "error";
        }
        model.addAttribute("book", book);
        return "payNow";
    }
    @GetMapping("/edit/{id}")
    public String getEditBookPage(@PathVariable Long id, Model model) {
        // Fetch the book from the database
        Book book = bookService.getBookById(id);

        // If the book does not exist, return an error
        if (book == null) {
            model.addAttribute("error", "Book not found");
            return "error"; // Ensure there's an error.html file in your templates folder
        }

        // Add the book to the model so it can be displayed in the HTML
        model.addAttribute("book", book);
        return "editBook"; // This is the Thymeleaf template file name
    }


}
