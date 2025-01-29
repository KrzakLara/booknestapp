package com.example.booknestapp.dto;

import com.example.booknestapp.entity.Book;

public class BookResponseInfoDto {
    private Long id;
    private String title;
    private String author;
    private double price;
    private boolean forSale;
    private boolean hasResponded;

    public BookResponseInfoDto(Book book, boolean hasResponded) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.price = book.getPrice();
        this.forSale = book.isForSale();
        this.hasResponded = hasResponded;
    }

    public BookResponseInfoDto(BookDto bookDto, boolean hasResponded) {
        this.id = bookDto.getId();
        this.title = bookDto.getTitle();
        this.author = bookDto.getAuthor();
        this.price = bookDto.getPrice();
        this.forSale = bookDto.isForSale();
        this.hasResponded = hasResponded;
    }


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }

    public boolean isForSale() {
        return forSale;
    }

    public boolean isHasResponded() {
        return hasResponded;
    }
}


