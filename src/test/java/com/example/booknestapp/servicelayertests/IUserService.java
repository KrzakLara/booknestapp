package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.cache.BookCache;
import com.example.booknestapp.dto.BookDto;
import com.example.booknestapp.dto.UserDto;
import com.example.booknestapp.entity.Book;
import com.example.booknestapp.entity.User;
import com.example.booknestapp.model.Role;
import com.example.booknestapp.repository.BookRepository;
import com.example.booknestapp.repository.UserJdbcRepository;
import com.example.booknestapp.repository.UserRepository;
import com.example.booknestapp.security.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IUserServiceTest {

    private IUserService userService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private BookRepository bookRepository;
    private BookCache bookCache;


    // Add this in your test class
    @Mock
    private UserJdbcRepository userJdbcRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock all dependencies
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        bookRepository = mock(BookRepository.class);
        bookCache = mock(BookCache.class);
        userJdbcRepository = mock(UserJdbcRepository.class); // New Mock

        // Include the new dependency in the constructor
        userService = new IUserService(userRepository, passwordEncoder, bookRepository, bookCache, userJdbcRepository);
    }


    @Test
    void saveUser_ShouldSaveValidUser() {
        UserDto userDto = new UserDto(null, "John", "Doe", "john.doe@example.com", "password123");
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");

        userService.saveUser(userDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveUser_ShouldThrowException_WhenEmailExists() {
        UserDto userDto = new UserDto(null, "John", "Doe", "john.doe@example.com", "password123");
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(new User());

        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(userDto));
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        User user = new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        User foundUser = userService.findByEmail(user.getEmail());

        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void findByEmail_ShouldReturnNull_WhenEmailDoesNotExist() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        User user = userService.findByEmail("nonexistent@example.com");

        assertNull(user);
    }

    @Test
    void findAllUsers_ShouldReturnAllUsers() {
        List<User> users = List.of(
                new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER),
                new User(2L, "Jane", "Doe", "jane.doe@example.com", "password", Role.USER)
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtos = userService.findAllUsers();

        assertEquals(2, userDtos.size());
    }

    @Test
    void updateUser_ShouldUpdateUser() {
        UserDto userDto = new UserDto(1L, "John", "Smith", null, "newPassword");
        User user = new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER);
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");

        userService.updateUser(userDto);

        assertEquals("John", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        UserDto userDto = new UserDto(1L, "John", "Smith", null, "newPassword");
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userDto));
    }

    @Test
    void findAllUsersWithBooks_ShouldReturnUsersWithBooks() {
        User user = new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER);
        Book book = new Book("Book Title", "Author", "Description", user, LocalDate.now(), true, 19.99);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(bookRepository.findByCreatedBy_Id(user.getId())).thenReturn(List.of(book));

        List<UserDto> userDtos = userService.findAllUsersWithBooks();

        assertEquals(1, userDtos.size());
        assertEquals(1, userDtos.get(0).getBooks().size());
    }

    @Test
    void getAllBooks_ShouldReturnCachedBooks() {
        Book book = new Book("Book Title", "Author", "Description", null, LocalDate.now(), true, 19.99);
        when(bookCache.getBooks()).thenReturn(List.of(book));

        List<BookDto> books = userService.getAllBooks();

        assertEquals(1, books.size());
        assertEquals("Book Title", books.get(0).getTitle());
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        User user = new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        var userDetails = userService.loadUserByUsername(user.getEmail());

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistent@example.com"));
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(new User());

        assertTrue(userService.existsByEmail("john.doe@example.com"));
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        assertFalse(userService.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void updateUserEmail_ShouldUpdateEmail_WhenUserExists() {
        User user = new User(1L, "John", "Doe", "old.email@example.com", "password", Role.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        boolean result = userService.updateUserEmail(1L, "new.email@example.com");

        assertTrue(result);
        assertEquals("new.email@example.com", user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserEmail_ShouldReturnFalse_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = userService.updateUserEmail(1L, "new.email@example.com");

        assertFalse(result);
    }

    @Test
    void updateUser_ShouldNotUpdatePassword_WhenPasswordIsNull() {
        UserDto userDto = new UserDto(1L, "John", "Smith", null, null);
        User user = new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER);

        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));

        userService.updateUser(userDto);

        assertEquals("password", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_ShouldNotUpdatePassword_WhenPasswordIsBlank() {
        UserDto userDto = new UserDto(1L, "John", "Smith", null, "   ");
        User user = new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER);

        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));

        userService.updateUser(userDto);

        assertEquals("password", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findAllUsersWithBooks_ShouldReturnEmptyBookList_WhenNoBooksExist() {
        User user = new User(1L, "John", "Doe", "john.doe@example.com", "password", Role.USER);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(bookRepository.findByCreatedBy_Id(user.getId())).thenReturn(List.of());

        List<UserDto> userDtos = userService.findAllUsersWithBooks();

        assertEquals(1, userDtos.size());
        assertTrue(userDtos.get(0).getBooks().isEmpty());
    }
}
