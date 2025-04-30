package service;


import com.faspix.dto.*;
import com.faspix.entity.Comment;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.exception.CommentNotFoundException;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.ValidationException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Test
    void addCommentTest_Success() {
        Event event = makeEventTest();
        event.setState(EventState.PUBLISHED);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));
        when(commentRepository.save(any()))
                .thenReturn(makeComment());

        RequestCommentDTO requestDTO = makeRequestComment();
        ResponseCommentDTO responseDTO = commentService.addComment("1",  "username", 1L, requestDTO);

        assertThat(responseDTO.getText(), equalTo(requestDTO.getText()));
        verify(eventRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addCommentTest_EventNotFound_Exception() {
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        RequestCommentDTO requestDTO = makeRequestComment();
        EventNotFoundException exception = assertThrowsExactly(EventNotFoundException.class,
                () -> commentService.addComment("1", "username", 1L, requestDTO)
        );
        assertThat(exception.getMessage(), equalTo("Event with id 1 not found"));
    }


    @Test
    void addCommentTest_EventNotPublished_Exception() {
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(makeEventTest()));

        RequestCommentDTO requestDTO = makeRequestComment();
        EventNotFoundException exception = assertThrowsExactly(EventNotFoundException.class,
                () -> commentService.addComment("1", "username", 1L, requestDTO)
        );
        assertThat(exception.getMessage(), equalTo("Event with id 1 not published yet"));

    }

    @Test
    void findCommentsByEventIdTest_Success() {
        Comment comment = makeComment();
        when(commentRepository.findByEvent_EventId(anyLong()))
                .thenReturn(List.of(comment));

        List<ResponseCommentDTO> comments = commentService.findCommentsByEventId(1L, 1, 10);
        assertThat(comments.size(), equalTo(1));
        assertThat(comments.getFirst().getText(), equalTo(comment.getText()));
        assertThat(comments.getFirst().getAuthor().getUserId(), equalTo(comment.getAuthorId()));
    }

    @Test
    void findCommentsByEventIdTest_NoComments_SuccessReturnNull() {
        when(commentRepository.findByEvent_EventId(anyLong()))
                .thenReturn(List.of());

        List<ResponseCommentDTO> responseDTO = commentService.findCommentsByEventId(1L, 1, 10);
        assertThat(responseDTO, is(Collections.emptyList()));
    }

//    @Test
//    void editCommentTest_Success_Author() {
//        Comment comment = makeComment();
//        comment.setAuthorId("1");
//        RequestCommentDTO requestDTO = makeRequestComment();
//
//        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
//        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
//
//        ResponseCommentDTO responseDTO = commentService.editComment("1", 1L, requestDTO);
//
//        assertThat(responseDTO.getText(), equalTo(comment.getText()));
//        verify(commentRepository, times(1)).findById(1L);
//        verify(commentRepository, times(1)).save(any(Comment.class));
//    }

    @Test
    void editCommentTest_CommentNotFound_Exception() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        RequestCommentDTO requestDTO = makeRequestComment();
        CommentNotFoundException exception = assertThrowsExactly(CommentNotFoundException.class,
                () -> commentService.editComment("1", 1L, requestDTO));
        assertThat(exception.getMessage(), equalTo("Comment with id 1 not found"));
    }

    @Test
    void deleteCommentTest_Success_Author() {
        Comment comment = makeComment();
        comment.setAuthorId("1");

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        commentService.deleteComment("1", 1L);

        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteCommentTest_CommentNotFound_Exception() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        CommentNotFoundException exception = assertThrowsExactly(CommentNotFoundException.class,
                () -> commentService.deleteComment("1", 1L));
        assertThat(exception.getMessage(), equalTo("Comment with id 1 not found"));
    }

    @Test
    void deleteCommentTest_UnauthorizedUser_Exception() {
        Comment comment = makeComment();
        comment.setAuthorId("2");

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getAuthorities())
            .thenReturn(List.of());

        ValidationException exception = assertThrowsExactly(ValidationException.class,
                () -> commentService.deleteComment("1", 1L));
        assertThat(exception.getMessage(), equalTo("User with id 1 is not authorized to edit comment with id " + comment.getId()));
    }

}
