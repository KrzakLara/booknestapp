package com.example.booknestapp.security.service;

import com.example.booknestapp.cache.BookCache;
import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.dto.UserDto;
import com.example.booknestapp.entity.User;
import com.example.booknestapp.model.Role;
import com.example.booknestapp.repository.BookRepository;
import com.example.booknestapp.repository.UserJdbcRepository;
import com.example.booknestapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IUserService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    private final BookCache bookCache;
    private final UserJdbcRepository userJdbcRepository;

    public IUserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            BookRepository bookRepository,
            BookCache bookCache,
            UserJdbcRepository userJdbcRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookRepository = bookRepository;
        this.bookCache = bookCache;
        this.userJdbcRepository = userJdbcRepository; // Assign Here
    }



    @Override
    public void saveUser(UserDto userDto) {
        if (existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        var user = new User(
                null,
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()),
                Role.USER
        );
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertEntityToDto)
                .toList();
    }

    private UserDto convertEntityToDto(User user) {
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), null);
    }

    @Override
    public void updateUser(UserDto userDto) {
        var user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        userRepository.save(user);
    }

    @Override
    public List<UserDto> findAllUsersWithBooks() {
        return userRepository.findAll().stream()
                .map(user -> {
                    List<BookDto> books = bookRepository.findByCreatedBy_Id(user.getId()).stream()
                            .map(book -> new BookDto(
                                    book.getId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    book.getDescription(),
                                    book.getPrice(),
                                    book.isForSale()
                            )).toList();

                    return new UserDto(
                            user.getId(),
                            books,
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            null
                    );
                }).toList();
    }

    @Override
    public List<BookDto> getAllBooks() {
        return bookCache.getBooks().stream()
                .map(book -> new BookDto(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getDescription(),
                        book.getPrice(),
                        book.isForSale()
                )).collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRole().getAuthorities()
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }


    @Override
    public boolean updateUserEmail(Long userId, String newEmail) {
        var userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return false; // User not found
        }
        User user = userOptional.get();
        user.setEmail(newEmail);
        userRepository.save(user);
        return true;
    }

    //jdbc
    @Override
    public List<UserDto> getUsersByStatus(String firstName) {
        List<User> users = userJdbcRepository.findUsersByFirstName(firstName);
        return users.stream()
                .map(this::convertEntityToDto)
                .toList();
    }


}
