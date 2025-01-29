package com.example.booknestapp.servicelayertests;

import com.example.booknestapp.entity.Book;
import com.example.booknestapp.entity.Choice;
import com.example.booknestapp.repository.ChoiceRepository;
import com.example.booknestapp.service.IChoiceService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IChoiceServiceTest {

    @Mock
    private ChoiceRepository choiceRepository;

    @InjectMocks
    private IChoiceService choiceService;

    public IChoiceServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveChoice() {
        Book book = new Book();
        String option = "Option 1";
        choiceService.saveChoice(option, book);
        verify(choiceRepository, times(1)).save(any(Choice.class));
    }

    @Test
    void testGetChoicesByPollId() {
        Long bookId = 1L;
        when(choiceRepository.findByBookId(bookId)).thenReturn(List.of(new Choice()));
        List<Choice> choices = choiceService.getChoicesByPollId(bookId);
        assertEquals(1, choices.size());
    }

    @Test
    void testGetChoiceById() {
        Long choiceId = 1L;
        Choice choice = new Choice();
        when(choiceRepository.findById(choiceId)).thenReturn(Optional.of(choice));
        assertEquals(choice, choiceService.getChoiceById(choiceId));
    }

    @Test
    void testGetChoicesByChoiceIds() {
        List<Long> choiceIds = List.of(1L, 2L);
        when(choiceRepository.findById(anyLong())).thenReturn(Optional.of(new Choice()));
        List<Choice> choices = choiceService.getChoicesByChoiceIds(choiceIds);
        assertEquals(2, choices.size());
    }

    @Test
    void testGetChoiceByChoiceId() {
        Long choiceId = 1L;
        when(choiceRepository.findById(choiceId)).thenReturn(Optional.of(new Choice()));
        assertNotNull(choiceService.getChoiceByChoiceId(choiceId));
    }

    @Test
    void testDeleteChoicesWithPollId() {
        Long bookId = 1L;
        choiceService.deleteChoicesWithPollId(bookId);
        verify(choiceRepository, times(1)).deleteChoiceByBook_Id(bookId);
    }
}
