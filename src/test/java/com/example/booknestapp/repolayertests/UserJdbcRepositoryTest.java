package com.example.booknestapp.repolayertests;

import com.example.booknestapp.entity.User;
import com.example.booknestapp.repository.UserJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserJdbcRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private UserJdbcRepository userJdbcRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userJdbcRepository = new UserJdbcRepository(jdbcTemplate);
    }

    @Test
    void testFindUsersByFirstName_WithValidFirstName() {
        // Arrange
        String firstName = "John";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");

        List<User> expectedUsers = List.of(mockUser);

        when(jdbcTemplate.query(
                eq("SELECT * FROM user_details WHERE firstName = ?"),
                eq(new Object[]{firstName}),
                any(RowMapper.class)
        )).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userJdbcRepository.findUsersByFirstName(firstName);

        // Assert
        assertEquals(expectedUsers, actualUsers, "The returned users should match the expected users.");
        verify(jdbcTemplate, times(1)).query(
                eq("SELECT * FROM user_details WHERE firstName = ?"),
                eq(new Object[]{firstName}),
                any(RowMapper.class)
        );
    }


    @Test
    void testFindUsersByStatus_WithNullStatus() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userJdbcRepository.findUsersByFirstName(null),
                "Expected IllegalArgumentException for null status.");
        assertEquals("Status cannot be null or blank.", exception.getMessage());
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void testFindUsersByStatus_WithBlankStatus() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userJdbcRepository.findUsersByFirstName("   "),
                "Expected IllegalArgumentException for blank status.");
        assertEquals("Status cannot be null or blank.", exception.getMessage());
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void testFindUsersByStatus_WithNoResults() {
        // Arrange
        String status = "INACTIVE";
        when(jdbcTemplate.query(
                eq("SELECT * FROM user_details WHERE status = ?"),
                eq(new Object[]{status}),
                any(RowMapper.class)
        )).thenReturn(List.of());

        // Act
        List<User> actualUsers = userJdbcRepository.findUsersByFirstName(status);

        // Assert
        assertTrue(actualUsers.isEmpty(), "The returned users list should be empty for an invalid status.");
        verify(jdbcTemplate, times(1)).query(
                eq("SELECT * FROM user_details WHERE firstName = ?"),
                eq(new Object[]{status}),
                any(RowMapper.class)
        );
    }

    @Test
    void testRowMapper_CorrectlyMapsFields() throws Exception {
        // Arrange
        var resultSet = mock(java.sql.ResultSet.class);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("first_name")).thenReturn("John");
        when(resultSet.getString("last_name")).thenReturn("Doe");
        when(resultSet.getString("email")).thenReturn("john.doe@example.com");

        RowMapper<User> rowMapper = (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            return user;
        };

        // Act
        User user = rowMapper.mapRow(resultSet, 1);

        // Assert
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void testJdbcTemplateQueryExecution() {
        // Arrange
        String status = "ACTIVE";
        String sql = "SELECT * FROM user_details WHERE firstName = ?";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");

        List<User> expectedUsers = List.of(mockUser);

        RowMapper<User> rowMapper = (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            return user;
        };

        when(jdbcTemplate.query(eq(sql), eq(new Object[]{status}), any(RowMapper.class)))
                .thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userJdbcRepository.findUsersByFirstName(status);

        // Assert
        assertEquals(expectedUsers, actualUsers, "The returned users should match the expected users.");
        verify(jdbcTemplate, times(1)).query(eq(sql), eq(new Object[]{status}), any(RowMapper.class));
    }

}
