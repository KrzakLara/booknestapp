package com.example.booknestapp.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;

@Entity
@NoArgsConstructor
@Data
public class Book {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String description;

    @JoinColumn(name = "creator_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "for_sale", nullable = false)
    private boolean forSale;

    @Column(nullable = false)
    private double price;

    @Builder
    public Book(String title, String author, String description, User createdBy, LocalDate createdAt, boolean forSale, double price) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt == null ? LocalDate.now(ZoneId.of("UTC")) : createdAt;
        this.forSale = forSale;
        this.price = price;
    }


}
