package com.example.booknestapp.repository;

import com.example.booknestapp.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCreatedBy_Id(Long userId);
    List<Book> findByCreatedByEmail(@Param("email") String email);

}
