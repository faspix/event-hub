package service;

import com.faspix.domain.entity.Event;
import com.faspix.domain.entity.EventDislike;
import com.faspix.domain.entity.EventLike;
import com.faspix.exception.ReactionAlreadyExistException;
import com.faspix.exception.ReactionNotExistException;
import com.faspix.repository.EventDislikeRepository;
import com.faspix.repository.EventLikeRepository;
import com.faspix.repository.EventRepository;
import com.faspix.service.EventReactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static utility.EventFactory.makeEventTest;

@ExtendWith(MockitoExtension.class)
public class EventReactionServiceTest {

    @Mock
    private EventLikeRepository eventLikeRepository;

    @Mock
    private EventDislikeRepository eventDislikeRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventReactionServiceImpl eventReactionService;

    private final String userId = "1";
    private final Long eventId = 1L;
    private Event event;

    @BeforeEach
    void setUp() {
        event = makeEventTest();
    }

    @Test
    void likeEvent_WhenUserAlreadyLiked_ThrowsException() {
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));
        when(eventLikeRepository.existsByEventAndAuthorId(event, userId))
                .thenReturn(true);

        ReactionAlreadyExistException exception = assertThrows(ReactionAlreadyExistException.class,
                () -> eventReactionService.likeEvent(userId, eventId)
        );
        assertThat(exception.getMessage(), equalTo("User with id 1 already liked event with id 1"));
    }

    @Test
    void likeEvent_WhenUserAlreadyDisliked_ThrowsException() {
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));
        when(eventLikeRepository.existsByEventAndAuthorId(event, userId))
                .thenReturn(false);
        when(eventDislikeRepository.existsByEventAndAuthorId(event, userId))
                .thenReturn(true);

        ReactionAlreadyExistException exception = assertThrows(ReactionAlreadyExistException.class,
                () -> eventReactionService.likeEvent(userId, eventId)
        );
        assertThat(exception.getMessage(), equalTo("User with id 1 already disliked event with id 1"));
    }

    @Test
    void dislikeEvent_WhenUserAlreadyLiked_ThrowsException() {
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));
        when(eventLikeRepository.existsByEventAndAuthorId(event, userId))
                .thenReturn(true);

        ReactionAlreadyExistException exception = assertThrows(ReactionAlreadyExistException.class,
                () -> eventReactionService.dislikeEvent(userId, eventId)
        );
        assertThat(exception.getMessage(), equalTo("User with id 1 already liked event with id 1"));
    }

    @Test
    void dislikeEvent_WhenUserAlreadyDisliked_ThrowsException() {
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));
        when(eventLikeRepository.existsByEventAndAuthorId(event, userId))
                .thenReturn(false);
        when(eventDislikeRepository.existsByEventAndAuthorId(event, userId))
                .thenReturn(true);

        ReactionAlreadyExistException exception = assertThrows(ReactionAlreadyExistException.class,
                () -> eventReactionService.dislikeEvent(userId, eventId)
        );
        assertThat(exception.getMessage(), equalTo("User with id 1 already disliked event with id 1"));
    }

    @Test
    void likeEvent_Success() {
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));
        when(eventLikeRepository.existsByEventAndAuthorId(event, userId))
                .thenReturn(false);
        when(eventDislikeRepository.existsByEventAndAuthorId(event, userId))
                .thenReturn(false);

        eventReactionService.likeEvent(userId, eventId);

        verify(eventLikeRepository, times(1)).save(any(EventLike.class));
        verify(eventRepository, times(1)).addLike(eventId);
    }

    @Test
    void dislikeEvent_Success() {
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));
        when(eventLikeRepository.existsByEventAndAuthorId(event, userId))
                .thenReturn(false);
        when(eventDislikeRepository.existsByEventAndAuthorId(event, userId))
                .thenReturn(false);

        eventReactionService.dislikeEvent(userId, eventId);

        verify(eventDislikeRepository, times(1)).save(any(EventDislike.class));
        verify(eventRepository, times(1)).addDislike(eventId);
    }

    @Test
    void removeLikeEvent_WhenNoLikeExists_ThrowsException() {
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));
        when(eventLikeRepository.findByAuthorIdAndEvent(userId, event))
                .thenReturn(Optional.empty());

        ReactionNotExistException exception = assertThrows(ReactionNotExistException.class,
                () -> eventReactionService.removeLikeEvent(userId, eventId)
        );
        assertThat(exception.getMessage(), equalTo("User with id 1 didn't like event with id 1"));
    }

    @Test
    void removeDislikeEvent_WhenNoLikeExists_ThrowsException() {
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));
        when(eventDislikeRepository.findByAuthorIdAndEvent(userId, event))
                .thenReturn(Optional.empty());

        ReactionNotExistException exception = assertThrows(ReactionNotExistException.class,
                () -> eventReactionService.removeDislikeEvent(userId, eventId)
        );
        assertThat(exception.getMessage(), equalTo("User with id 1 didn't dislike event with id 1"));
    }


    @Test
    void removeLikeEvent_Success() {
        EventLike eventLike = new EventLike(userId, event);
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));
        when(eventLikeRepository.findByAuthorIdAndEvent(userId, event))
                .thenReturn(Optional.of(eventLike));

        eventReactionService.removeLikeEvent(userId, eventId);

        verify(eventLikeRepository, times(1)).delete(eventLike);
        verify(eventRepository, times(1)).removeLike(eventId);
    }


    @Test
    void removeDislikeEvent_Success() {
        EventDislike eventDislike = new EventDislike(userId, event);
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));
        when(eventDislikeRepository.findByAuthorIdAndEvent(userId, event))
                .thenReturn(Optional.of(eventDislike));

        eventReactionService.removeDislikeEvent(userId, eventId);

        verify(eventDislikeRepository, times(1)).delete(eventDislike);
        verify(eventRepository, times(1)).removeDislike(eventId);
    }


}
