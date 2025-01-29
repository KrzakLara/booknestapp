package com.example.booknestapp.repository;

import com.example.booknestapp.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    boolean existsByChoice_Book_IdAndRespondent_Id(Long bookId, Long responderId);
    List<Response> findAllByChoice_Book_Id(Long bookId);
    void deleteResponsesByChoice_Book_Id(Long bookId);
}