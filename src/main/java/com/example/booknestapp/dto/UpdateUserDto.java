package com.example.booknestapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateUserDto {
    private Long id;
    private @NotEmpty String firstName;
    private @NotEmpty String lastName;
    private @NotEmpty(message = "Email should not be empty")
    @Email String email;
    private String password; // Optional for updates
}
