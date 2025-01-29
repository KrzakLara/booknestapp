package com.example.booknestapp.service;

import com.example.booknestapp.entity.Book;
import com.example.booknestapp.entity.Choice;
import com.example.booknestapp.repository.ChoiceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IChoiceService implements ChoiceService {

    private final ChoiceRepository choiceRepository;

    @Override
    public void saveChoice(String option, Book book) {
        var choice = new Choice(book, option, 0L);
        choiceRepository.save(choice);
    }

    @Override
    public List<Choice> getChoicesByPollId(Long bookId) {
        return choiceRepository.findByBookId(bookId);
    }

    @Override
    public Choice getChoiceById(Long choiceId) {
        return choiceRepository.findById(choiceId).orElseThrow(() -> new IllegalStateException("value can't be null"));
    }

    @Override
    public List<Choice> getChoicesByChoiceIds(List<Long> choiceIds) {
        var choices = choiceIds.stream().map(choiceRepository::findById).toList();
        return choices.stream()
                .map(choice -> choice
                        .orElseThrow(() -> new IllegalStateException("value can't be null"))).toList();
    }

    @Override
    public Choice getChoiceByChoiceId(Long choiceId) {
        var choice = choiceRepository.findById(choiceId);
        return choice.orElseThrow(() -> new IllegalStateException("value can't be null"));
    }

    @Override
    public void deleteChoicesWithPollId(Long id) {
        choiceRepository.deleteChoiceByBook_Id(id);
    }
}
