package service;


import com.faspix.client.UserServiceClient;
import com.faspix.dto.*;
import com.faspix.entity.Comment;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.exception.EventNotFoundException;
import com.faspix.mapper.CommentMapper;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.CommentRepository;
import com.faspix.repository.EventRepository;
import com.faspix.service.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static utility.CommentFactory.*;
import static utility.EventFactory.*;
import static utility.UserFactory.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Spy
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void addCommentTest_Success() {
        Event event = makeEventTest();
        event.setState(EventState.PUBLISHED);
        when(userServiceClient.getUserById(any()))
                .thenReturn(makeResponseUserTest());
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));
        when(commentRepository.save(any()))
                .thenReturn(makeComment());

        RequestCommentDTO requestDTO = makeRequestComment();
        ResponseCommentDTO responseDTO = commentService.addComment("1", 1L, requestDTO);

        assertThat(responseDTO.getText(), equalTo(requestDTO.getText()));
        verify(userServiceClient, times(1)).getUserById(any());
        verify(eventRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addCommentTest_EventNotFound_Exception() {
        when(userServiceClient.getUserById(any()))
                .thenReturn(makeResponseUserTest());
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        RequestCommentDTO requestDTO = makeRequestComment();
        EventNotFoundException exception = assertThrowsExactly(EventNotFoundException.class,
                () -> commentService.addComment("1", 1L, requestDTO)
        );
        assertThat(exception.getMessage(), equalTo("Event with id 1 not found"));
    }


    @Test
    void addCommentTest_EventNotPublished_Exception() {
        when(userServiceClient.getUserById(any()))
                .thenReturn(makeResponseUserTest());
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(makeEventTest()));

        RequestCommentDTO requestDTO = makeRequestComment();
        EventNotFoundException exception = assertThrowsExactly(EventNotFoundException.class,
                () -> commentService.addComment("1", 1L, requestDTO)
        );
        assertThat(exception.getMessage(), equalTo("Event with id 1 not published yet"));

    }

    @Test
    void findCommentsByEventIdTest_Success() {
        Comment comment = makeComment();
        when(commentRepository.findCommentsByEventId(anyLong()))
                .thenReturn(List.of(comment));
        when(userServiceClient.getUsersByIds(any()))
                .thenReturn(Set.of(makeResponseShortUser()));

        List<ResponseCommentDTO> comments = commentService.findCommentsByEventId(1L);
        assertThat(comments.size(), equalTo(1));
        assertThat(comments.getFirst().getText(), equalTo(comment.getText()));
        assertThat(comments.getFirst().getAuthor().getUserId(), equalTo(comment.getAuthorId()));
    }

    @Test
    void findCommentsByEventIdTest_NoComments_SuccessReturnNull() {
        when(commentRepository.findCommentsByEventId(anyLong()))
                .thenReturn(List.of());

        List<ResponseCommentDTO> responseDTO = commentService.findCommentsByEventId(1L);
        assertThat(responseDTO, is(Collections.emptyList()));
    }



}
