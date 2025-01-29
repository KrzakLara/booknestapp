package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.entity.Choice;
import com.example.booknestapp.entity.Response;
import com.example.booknestapp.entity.User;
import com.example.booknestapp.repository.ResponseRepository;
import com.example.booknestapp.service.IResponseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IResponseServiceTest {

    private IResponseService iResponseService;

    @Mock
    private ResponseRepository responseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        iResponseService = new IResponseService(responseRepository);
    }

    @Test
    void testHasResponded() {
        Long bookId = 1L;
        Long responderId = 2L;

        when(responseRepository.existsByChoice_Book_IdAndRespondent_Id(bookId, responderId)).thenReturn(true);

        boolean result = iResponseService.hasResponded(bookId, responderId);

        assertTrue(result, "The user should have responded to the book");
        verify(responseRepository, times(1)).existsByChoice_Book_IdAndRespondent_Id(bookId, responderId);
    }

    @Test
    void testSaveResponse() {
        Choice choice = mock(Choice.class);
        User responder = mock(User.class);

        iResponseService.saveResponse(choice, responder);

        verify(responseRepository, times(1)).save(any(Response.class));
    }

    @Test
    void testSaveResponses() {
        Choice choice1 = mock(Choice.class);
        Choice choice2 = mock(Choice.class);
        User responder = mock(User.class);

        List<Choice> choices = List.of(choice1, choice2);

        iResponseService.saveResponses(choices, responder);

        verify(responseRepository, times(choices.size())).save(any(Response.class));
    }

    @Test
    void testGetPollResults() {
        Long bookId = 1L;

        Choice choice1 = mock(Choice.class);
        Choice choice2 = mock(Choice.class);

        when(choice1.getOption()).thenReturn("Option 1");
        when(choice2.getOption()).thenReturn("Option 2");

        Response response1 = new Response(mock(User.class), choice1);
        Response response2 = new Response(mock(User.class), choice1);
        Response response3 = new Response(mock(User.class), choice2);

        when(responseRepository.findAllByChoice_Book_Id(bookId)).thenReturn(List.of(response1, response2, response3));

        Map<String, Long> results = iResponseService.getPollResults(bookId);

        assertNotNull(results, "Poll results should not be null");
        assertEquals(2L, results.get("Option 1"), "Option 1 count should match");
        assertEquals(1L, results.get("Option 2"), "Option 2 count should match");
        verify(responseRepository, times(1)).findAllByChoice_Book_Id(bookId);
    }

    @Test
    void testDeleteResponsesWithPollId() {
        Long bookId = 1L;

        iResponseService.deleteResponsesWithPollId(bookId);

        verify(responseRepository, times(1)).deleteResponsesByChoice_Book_Id(bookId);
    }
}
