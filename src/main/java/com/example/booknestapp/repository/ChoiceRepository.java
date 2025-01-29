package com.example.booknestapp.repository;

import com.example.booknestapp.entity.Choice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findByBookId(Long bookId);
    Long countByBookIdAndId(Long bookId, Long choiceId);
    void deleteChoiceByBook_Id(Long id);
}
