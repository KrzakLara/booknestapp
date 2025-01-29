package com.example.booknestapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "choice_id", nullable = false)
    private Choice choice;

    @ManyToOne
    @JoinColumn(name = "respondent_id", nullable = false)
    private User respondent;

    public Response(User respondent, Choice choice) {
        this.respondent = respondent;
        this.choice = choice;
    }
}
