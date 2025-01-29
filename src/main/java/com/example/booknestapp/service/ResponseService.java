package com.example.booknestapp.service;

import com.example.booknestapp.entity.Choice;
import com.example.booknestapp.entity.User;
import java.util.List;
import java.util.Map;

public interface ResponseService {

    boolean hasResponded(Long pollId, Long responderId);
    void saveResponse(Choice choice, User responder);
    void saveResponses(List<Choice> choice, User responder);
    Map<String, Long> getPollResults(Long id);

    void deleteResponsesWithPollId(Long id);
}
