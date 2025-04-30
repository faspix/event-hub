package service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.faspix.client.CategoryServiceClient;
import com.faspix.client.StatisticsServiceClient;
import com.faspix.dto.*;
import com.faspix.entity.Event;
import com.faspix.entity.EventIndex;
import com.faspix.enums.EventState;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.EventNotPublishedException;
import com.faspix.exception.ValidationException;
import com.faspix.mapper.EventMapper;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.EventRepository;
import com.faspix.repository.EventSearchRepository;
import com.faspix.service.*;
import com.faspix.utility.EventSortType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private EventServiceImpl eventService;

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
    private SearchServiceImpl searchService;

    @Mock
    private HitsMetadata<EventIndex> hitsMetadata;

    @Mock
    private Hit<EventIndex> hit;

    @Mock
    private EventSearchRepository eventSearchRepository;

    @Spy
    private final EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Spy
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

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
        assertThat(updatedEvent.getLocation(), equalTo(event.getLocation()));
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
        assertThat(updatedEvent.getLocation(), equalTo(event.getLocation()));
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
    public void findEventTest_Success() {
        Event event = makeEventTest();
        event.setState(EventState.PUBLISHED);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));

        ResponseEventDTO result = eventService.findEventById(1L, new MockHttpServletRequest());

        assertThat(result.getEventId(), equalTo(event.getEventId()));
        assertThat(result.getEventDate(), equalTo(event.getEventDate()));
        assertThat(result.getTitle(), equalTo(event.getTitle()));
        assertThat(result.getAnnotation(), equalTo(event.getAnnotation()));
        assertThat(result.getDescription(), equalTo(event.getDescription()));
        assertThat(result.getLocation(), equalTo(event.getLocation()));
        assertThat(result.getCreationDate(), equalTo(event.getCreationDate()));
        assertThat(result.getPublishedAt(), equalTo(event.getPublishedAt()));
        assertThat(result.getPaid(), equalTo(event.getPaid()));
        assertThat(result.getState(), equalTo(event.getState()));
    }

    @Test
    public void findEventByIdTest_EventNotPublished_Exception() {
        Event event = makeEventTest();
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));

        EventNotPublishedException exception = Assertions.assertThrowsExactly(EventNotPublishedException.class, () ->
                eventService.findEventById(1L, new MockHttpServletRequest())
        );

        assertEquals("Event with id 1 not published yet", exception.getMessage());
    }


    @Test
    public void findEventByIdTest_EventNotFound_Exception() {
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        EventNotFoundException exception = Assertions.assertThrowsExactly(EventNotFoundException.class, () ->
                eventService.findEventById(1L, new MockHttpServletRequest())
        );

        assertEquals("Event with id 1 not found", exception.getMessage());
    }

    @Test
    public void findAllUsersEventsTest_Success() {
        List<Event> events = List.of(makeEventTest(), makeEventTest());
        when(eventRepository.findEventsByInitiatorId(any(), any())).thenReturn(new PageImpl<>(events));

        List<ResponseEventShortDTO> result = searchService.findAllUsersEvents("1", 0, 10);

        assertThat(result.size(), equalTo(2));
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
        assertThat(eventFromRepo.getLocation(), equalTo(updatedEvent.getLocation()));
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
        assertThat(eventFromRepo.getLocation(), equalTo(updatedEvent.getLocation()));
        assertThat(eventFromRepo.getPaid(), equalTo(updatedEvent.getPaid()));
        assertThat(eventFromRepo.getState(), equalTo(updatedEvent.getState()));

        verify(eventRepository, times(1)).save(any());
    }

    @Test
    public void findEventsTest_SortByEventDate_Success() throws IOException {
        Event event = makeEventTest();
        event.setEventId(1L);
        List<Event> events = List.of(event);
        when(eventRepository.findAllById(any()))
                .thenReturn(events);
        when(hit.id()).thenReturn("1"); // elasticsearch
        when(hitsMetadata.hits())
                .thenReturn(List.of(hit));
        when(searchResponse.hits())
                .thenReturn(hitsMetadata);
        when(elasticsearchClient.search(any(SearchRequest.class), eq(EventIndex.class)))
                .thenReturn(searchResponse);

        List<ResponseEventShortDTO> result = searchService.findEvents(
                "test", List.of(1L), true, null, null, true,
                EventSortType.EVENT_DATE, 0, 10);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getTitle(), equalTo(events.get(0).getTitle()));;
    }

    @Test
    public void findEventsTest_SortByViews_Success() throws IOException {
        Event event = makeEventTest();
        event.setEventId(1L);
        List<Event> events = List.of(event);
        when(eventRepository.findAllById(any()))
                .thenReturn(events);
        when(hit.id()).thenReturn("1"); // elasticsearch
        when(hitsMetadata.hits())
                .thenReturn(List.of(hit));
        when(searchResponse.hits())
                .thenReturn(hitsMetadata);
        when(elasticsearchClient.search(any(SearchRequest.class), eq(EventIndex.class)))
                .thenReturn(searchResponse);

        List<ResponseEventShortDTO> result = searchService.findEvents(
                "test", List.of(1L), true, null, null, true,
                EventSortType.VIEWS, 0, 10);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getTitle(), equalTo(events.get(0).getTitle()));
    }

    @Test
    public void findEventsAdminTest_Success() {
        List<Event> events = List.of(makeEventTest());
        Page<Event> eventPage = new PageImpl<>(events);
        when(eventRepository.searchEventAdmin(anyList(), anyList(), anyList(), any(), any(), any()))
                .thenReturn(eventPage);

        List<ResponseEventDTO> result = searchService.findEventsAdmin(
                List.of("1"), List.of(EventState.PENDING), List.of(1L), null, null, 0, 10);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getTitle(), equalTo(events.get(0).getTitle()));
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
    public void findEventsByIdsTest_Success() {
        List<Event> events = List.of(makeEventTest());
        Set<Long> eventIds = Set.of(1L);
        when(eventRepository.findAllById(eventIds)).thenReturn(events);

        List<ResponseEventShortDTO> result = eventService.findEventsByIds(eventIds);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getTitle(), equalTo(events.get(0).getTitle()));
        verify(eventRepository).findAllById(eventIds);
    }



}
