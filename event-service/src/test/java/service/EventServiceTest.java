package service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.faspix.client.CategoryServiceClient;
import com.faspix.client.StatisticsServiceClient;
import com.faspix.domain.entity.Event;
import com.faspix.domain.index.EventIndex;
import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.RequestUpdateEventAdminDTO;
import com.faspix.exception.ValidationException;
import com.faspix.mapper.EventMapper;
import com.faspix.repository.EventRepository;
import com.faspix.repository.EventSearchRepository;
import com.faspix.service.*;
import com.faspix.shared.dto.ConfirmedRequestsDTO;
import com.faspix.shared.dto.ResponseEventDTO;
import com.faspix.shared.dto.UpdateCategoryNameDTO;
import com.faspix.shared.dto.UpdateUsernameDTO;
import com.faspix.shared.utility.EventState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static utility.CategoryFactory.makeResponseCategory;
import static utility.EventFactory.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private EventService eventService;

    @Mock
    private SearchResponse<EventIndex> searchResponse;

    @Mock
    private EndpointStatisticsService endpointStatisticsService;

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @Mock
    private EventViewService eventViewService;

    @Mock
    private StatisticsServiceClient statisticsServiceClient;

    @Mock
    private CategoryServiceClient categoryServiceClient;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private EventSearchService searchService;

    @Mock
    private HitsMetadata<EventIndex> hitsMetadata;

    @Mock
    private Hit<EventIndex> hit;

    @Mock
    private EventSearchRepository eventSearchRepository;

    @Spy
    private final EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Test
    public void createEventTest_Success() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        when(eventRepository.save(any()))
                .thenReturn(makeEventTest());
        when(categoryServiceClient.getCategoryById(any()))
                .thenReturn((makeResponseCategory()));

        ResponseEventDTO event = eventService.createEvent("1", "username", requestEventDTO);

        assertThat(event.getEventDate(), equalTo(requestEventDTO.getEventDate()));
        assertThat(event.getAnnotation(), equalTo(requestEventDTO.getAnnotation()));
        assertThat(event.getRequestModeration(), equalTo(requestEventDTO.getRequestModeration()));
        assertThat(event.getPaid(), equalTo(requestEventDTO.getPaid()));
        assertThat(event.getDescription(), equalTo(requestEventDTO.getDescription()));
        assertThat(event.getParticipantLimit(), equalTo(requestEventDTO.getParticipantLimit()));
        assertThat(event.getTitle(), equalTo(requestEventDTO.getTitle()));
        assertThat(event.getState(), equalTo(EventState.PENDING));

        verify(eventRepository, times(1)).save(any());

    }

    @Test
    public void createEventTest_EventDateIsTooSoon_Success() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(OffsetDateTime.now().plusHours(2).plusMinutes(1));
        when(eventRepository.save(any()))
                .thenReturn(makeEventTest());
        when(categoryServiceClient.getCategoryById(any()))
                .thenReturn((makeResponseCategory()));

        ResponseEventDTO event = eventService.createEvent("1", "username", requestEventDTO);

        assertThat(event.getAnnotation(), equalTo(requestEventDTO.getAnnotation()));
        assertThat(event.getRequestModeration(), equalTo(requestEventDTO.getRequestModeration()));
        assertThat(event.getPaid(), equalTo(requestEventDTO.getPaid()));
        assertThat(event.getDescription(), equalTo(requestEventDTO.getDescription()));
        assertThat(event.getParticipantLimit(), equalTo(requestEventDTO.getParticipantLimit()));
        assertThat(event.getTitle(), equalTo(requestEventDTO.getTitle()));
        assertThat(event.getState(), equalTo(EventState.PENDING));

        verify(eventRepository, times(1)).save(any());
    }

    @Test
    public void createEventTest_EventDateIsTooSoon_Exception() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(OffsetDateTime.now());

        ValidationException exception = Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventService.createEvent("1", "username", requestEventDTO)
        );
        assertEquals("Event cannot start in less than 2 hours", exception.getMessage());
    }

    @Test
    public void editEventTest_Success() {
        Event event = makeEventTest();
        event.setTitle("updated event title");
        RequestEventDTO updateRequest = makeRequestEventTest();
        updateRequest.setTitle(event.getTitle());
        when(eventRepository.save(any()))
                .thenReturn(event);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(makeEventTest()));

        ResponseEventDTO updatedEvent = eventService.editEvent("1", 1L, updateRequest);

        assertThat(updatedEvent.getTitle(), equalTo(event.getTitle()));
        assertThat(updatedEvent.getAnnotation(), equalTo(event.getAnnotation()));
        assertThat(updatedEvent.getDescription(), equalTo(event.getDescription()));
        assertThat(updatedEvent.getPaid(), equalTo(event.getPaid()));
        assertThat(updatedEvent.getState(), equalTo(event.getState()));

        verify(eventRepository, times(1)).save(any());
    }


    @Test
    public void editEventTest_EventStartsTooSoon_Exception() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(OffsetDateTime.now().plusHours(2));

        ValidationException exception = Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventService.editEvent("1", 1L, requestEventDTO)
        );
        assertEquals("Event cannot start in less than 2 hours", exception.getMessage());
    }


    @Test
    public void editEventTest_EventStartsTooSoon_Success() {
        Event event = makeEventTest();
        event.setTitle("updated event title");
        event.setEventDate(OffsetDateTime.now().plusHours(2).plusMinutes(1));
        RequestEventDTO updateRequest = makeRequestEventTest();
        updateRequest.setTitle(event.getTitle());
        updateRequest.setEventDate(event.getEventDate());
        when(eventRepository.save(any()))
                .thenReturn(event);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(makeEventTest()));

        ResponseEventDTO updatedEvent = eventService.editEvent("1", 1L, updateRequest);

        assertThat(updatedEvent.getEventDate(), equalTo(event.getEventDate()));
        assertThat(updatedEvent.getTitle(), equalTo(event.getTitle()));
        assertThat(updatedEvent.getAnnotation(), equalTo(event.getAnnotation()));
        assertThat(updatedEvent.getDescription(), equalTo(event.getDescription()));
        assertThat(updatedEvent.getPaid(), equalTo(event.getPaid()));
        assertThat(updatedEvent.getState(), equalTo(event.getState()));

        verify(eventRepository, times(1)).save(any());
    }

    @Test
    public void editEventTest_UserDidntCreateEvent_Exception() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        Event event = makeEventTest();
        event.setInitiatorId("22");
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));

        ValidationException exception = Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventService.editEvent("1", 1L, requestEventDTO)
        );
        assertEquals("User with id 1 didn't create event with id 1", exception.getMessage());
    }

    @Test
    public void editEventTest_PublishedEvent_Exception() {
        Event event = makeEventTest();
        event.setState(EventState.PUBLISHED);
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));

        ValidationException exception = Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventService.editEvent("1", 1L, requestEventDTO)
        );
        assertEquals("Event must not be published", exception.getMessage());
    }


    @Test
    public void adminEditEventTest_Success() {
        Event event = makeEventTest();
        Event eventFromRepo = makeEventTest();
        eventFromRepo.setState(EventState.PUBLISHED);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(any()))
                .thenReturn(eventFromRepo);

        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();

        ResponseEventDTO updatedEvent = eventService.adminEditEvent(1L, adminRequest);

        assertThat(eventFromRepo.getEventId(), equalTo(updatedEvent.getEventId()));
        assertThat(eventFromRepo.getTitle(), equalTo(updatedEvent.getTitle()));
        assertThat(eventFromRepo.getAnnotation(), equalTo(updatedEvent.getAnnotation()));
        assertThat(eventFromRepo.getDescription(), equalTo(updatedEvent.getDescription()));
        assertThat(eventFromRepo.getPaid(), equalTo(updatedEvent.getPaid()));
        assertThat(eventFromRepo.getState(), equalTo(updatedEvent.getState()));

        verify(eventRepository, times(1)).save(any());
    }

    @Test
    public void adminEditEventTest_EventStartTooSoon_Exception() {
        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();
        Event event = makeEventTest();
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));
        event.setEventDate(OffsetDateTime.now().plusHours(1));

        ValidationException exception = Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventService.adminEditEvent(1L, adminRequest)
        );
        assertEquals("Event with id 1 starts in less than an hour", exception.getMessage());
    }

    @Test
    public void adminEditEventTest_EventStartsToSoon_Success() {
        Event event = makeEventTest();
        event.setEventDate(OffsetDateTime.now().plusHours(1).plusMinutes(1));
        Event eventFromRepo = makeEventTest();
        eventFromRepo.setState(EventState.PUBLISHED);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(any()))
                .thenReturn(eventFromRepo);

        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();

        ResponseEventDTO updatedEvent = eventService.adminEditEvent(1L, adminRequest);

        assertThat(eventFromRepo.getEventId(), equalTo(updatedEvent.getEventId()));
        assertThat(eventFromRepo.getTitle(), equalTo(updatedEvent.getTitle()));
        assertThat(eventFromRepo.getAnnotation(), equalTo(updatedEvent.getAnnotation()));
        assertThat(eventFromRepo.getDescription(), equalTo(updatedEvent.getDescription()));
        assertThat(eventFromRepo.getPaid(), equalTo(updatedEvent.getPaid()));
        assertThat(eventFromRepo.getState(), equalTo(updatedEvent.getState()));

        verify(eventRepository, times(1)).save(any());
    }

    @Test
    public void setConfirmedRequestsNumberTest_Success() {
        Event event = makeEventTest();
        event.setConfirmedRequests(5);
        ConfirmedRequestsDTO requestsDTO = new ConfirmedRequestsDTO(1L, 3);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenReturn(event);

        eventService.setConfirmedRequestsNumber(requestsDTO);

        assertThat(event.getConfirmedRequests(), equalTo(8));
        verify(eventRepository).save(event);
    }

    @Test
    public void updateInitiatorUsernameTest_Success() {
        eventService.updateInitiatorUsername(new UpdateUsernameDTO("1", "username"));
    }

    @Test
    public void updateCategoryNameTest_Success() {
        eventService.updateCategoryName(new UpdateCategoryNameDTO(1L, "username"));
    }


}
