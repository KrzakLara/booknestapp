package com.example.booknestapp.dto;

import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String description;
    private double price;
    private boolean forSale;


    public BookDto(Long id, String title, String author, String description, double price, boolean forSale) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.forSale = forSale;
    }
}
