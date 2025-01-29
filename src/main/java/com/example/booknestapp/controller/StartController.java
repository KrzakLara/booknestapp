package com.example.booknestapp.controller;

import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.security.service.AuthenticationService;
import com.example.booknestapp.service.BookService;
import com.example.booknestapp.security.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StartController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final BookService bookService;

    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/home")
    public String home(Model model) {

        var userEmail = authenticationService.getAuthenticatedUserEmail();


        var user = userService.findByEmail(userEmail);
        model.addAttribute("user", user);


        var userBooks = bookService.getBooksByUser(userEmail);
        model.addAttribute("pollResponseInfo", userBooks);

        return "home";
    }
}
