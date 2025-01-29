package com.example.booknestapp.controller;

import com.example.booknestapp.dto.UserDto;
import com.example.booknestapp.security.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers();
    }

    //jdbc
    @GetMapping("/status/{firstname}")
    public List<UserDto> findUsersByFirstName(@PathVariable String firstName) {
        return userService.getUsersByStatus(firstName);
    }

    @PutMapping("/{id}/email")
    public String updateUserEmail(@PathVariable Long id, @RequestParam String newEmail) {
        boolean success = userService.updateUserEmail(id, newEmail);
        return success ? "Email updated successfully" : "Failed to update email";
    }


    @PostMapping
    public String saveUser(@RequestBody UserDto userDto) {
        userService.saveUser(userDto);
        return "User saved successfully.";
    }

    @GetMapping("/{email}")
    public UserDto getUserByEmail(@PathVariable String email) {
        var user = userService.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null
        );
    }


}
