package com.example.booknestapp.service;

import com.example.booknestapp.entity.Choice;
import com.example.booknestapp.entity.Response;
import com.example.booknestapp.entity.User;
import com.example.booknestapp.repository.ResponseRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IResponseService implements ResponseService {

    private final ResponseRepository responseRepository;

    public boolean hasResponded(Long bookId, Long responderId) {
        return responseRepository.existsByChoice_Book_IdAndRespondent_Id(bookId, responderId); // Updated
    }

    @Override
    public void saveResponse(Choice choice, User responder) {
        responseRepository.save(getResponse(responder, choice));
    }

    @Override
    public void saveResponses(List<Choice> choices, User responder) {
        choices.forEach(c -> responseRepository.save(getResponse(responder, c)));
    }

    private static Response getResponse(User responder, Choice choice) {
        return new Response(responder, choice);
    }

    @Override
    public Map<String, Long> getPollResults(Long bookId) {
        var responses = responseRepository.findAllByChoice_Book_Id(bookId); // Updated
        return responses.stream().collect(Collectors.groupingBy(r -> r.getChoice().getOption(), Collectors.counting()));
    }

    @Override
    public void deleteResponsesWithPollId(Long bookId) {
        responseRepository.deleteResponsesByChoice_Book_Id(bookId); // Updated
    }
}
