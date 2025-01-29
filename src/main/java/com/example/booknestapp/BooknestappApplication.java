package com.example.booknestapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BooknestappApplication {

    public static void main(String[] args) {
        SpringApplication.run(BooknestappApplication.class, args);
    }

}
