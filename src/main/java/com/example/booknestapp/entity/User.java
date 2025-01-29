package com.example.booknestapp.entity;

import com.example.booknestapp.model.Role;
import jakarta.persistence.*;
import java.text.Normalizer;
import java.util.Collection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="user_details")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    public User(Long id, String firstName, String lastName, String email, String password, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = resolveUsername();
        this.email = email;
        this.password = password;
        this.role = role;
    }

    private String resolveUsername() {
        var firstNameClear = Normalizer.normalize(firstName.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        var lastNameClear = Normalizer.normalize(lastName.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        return firstNameClear.charAt(0) + lastNameClear;
    }
}
