package com.example.booknestapp.service;

import com.example.booknestapp.entity.Book;
import com.example.booknestapp.entity.Choice;

import java.util.List;

public interface ChoiceService {

    void saveChoice(String choice, Book book);
    List<Choice> getChoicesByPollId(Long pollId);
    Choice getChoiceById(Long choiceId);
    List<Choice> getChoicesByChoiceIds(List<Long> choiceIds);
    Choice getChoiceByChoiceId(Long choiceId);

    void deleteChoicesWithPollId(Long id);
}
