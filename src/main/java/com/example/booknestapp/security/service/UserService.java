package com.example.booknestapp.security.service;

import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.dto.UserDto;
import com.example.booknestapp.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findByEmail(String email);

    List<UserDto> findAllUsers();

    void updateUser(UserDto userDto);

    List<UserDto> findAllUsersWithBooks();

    List<BookDto> getAllBooks();

    UserDetails loadUserByUsername(String email);

    boolean existsByEmail(String email);

    boolean updateUserEmail(Long userId, String newEmail);

    List<UserDto> getUsersByStatus(String status);
}
