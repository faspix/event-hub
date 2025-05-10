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
import org.springframework.data.domain.Pageable;
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
public class SearchServiceTest {

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
    public void findEventTest_Success() {
        Event event = makeEventTest();
        event.setState(EventState.PUBLISHED);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));

        ResponseEventDTO result = searchService.findEventById(1L, new MockHttpServletRequest());

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
                searchService.findEventById(1L, new MockHttpServletRequest())
        );

        assertEquals("Event with id 1 not published yet", exception.getMessage());
    }

    @Test
    public void findEventByIdTest_EventNotFound_Exception() {
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        EventNotFoundException exception = Assertions.assertThrowsExactly(EventNotFoundException.class, () ->
                searchService.findEventById(1L, new MockHttpServletRequest())
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
                EventSortType.NONE, 0, 10);

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
    public void findEventsByIdsTest_Success() {
        List<Event> events = List.of(makeEventTest());
        Set<Long> eventIds = Set.of(1L);
        when(eventRepository.findAllEventsByIds(anySet(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(events));

        List<ResponseEventShortDTO> result = searchService.findEventsByIds(eventIds, 0, 10);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getTitle(), equalTo(events.get(0).getTitle()));
        verify(eventRepository).findAllEventsByIds(anySet(), any(Pageable.class));
    }

}
