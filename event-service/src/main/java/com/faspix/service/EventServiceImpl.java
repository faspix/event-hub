package com.faspix.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.faspix.client.CategoryServiceClient;
import com.faspix.client.StatisticsServiceClient;
import com.faspix.client.UserServiceClient;
import com.faspix.dto.*;
import com.faspix.entity.Event;
import com.faspix.entity.EventIndex;
import com.faspix.enums.EventState;
import com.faspix.enums.EventStateAction;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.EventNotPublishedException;
import com.faspix.exception.ValidationException;
import com.faspix.mapper.EventMapper;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.EventRepository;
import com.faspix.repository.EventSearchRepository;
import com.faspix.utility.EventSortType;
import com.faspix.utility.SafeElasticSearch;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;

    private final UserServiceClient userServiceClient;

    private final CategoryServiceClient categoryServiceClient;

    private final StatisticsServiceClient statisticsServiceClient;

    private final EventRepository eventRepository;

    private final EndpointStatisticsService endpointStatisticsService;

    private final UserMapper userMapper;

    private final CommentService commentService;

    private final CacheManager cacheManager;

    private final EventSearchRepository eventSearchRepository;

    private final SafeElasticSearch safeElasticSearch;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEventDTO createEvent(String creatorId, RequestEventDTO eventDTO) {
        if (eventDTO.getEventDate().isBefore(OffsetDateTime.now().plusHours(2)))
            throw new ValidationException("Event cannot start in less than 2 hours");
        getUserById(creatorId);
        Event event = eventMapper.requestToEvent(eventDTO);

        event.setConfirmedRequests(0);
        event.setCreationDate(OffsetDateTime.now());
        event.setInitiatorId(creatorId);
        event.setState(EventState.PENDING);
        event.setLikes(0);
        event.setDislikes(0);

        eventRepository.save(event);
        eventSearchRepository.save(
                eventMapper.eventToIndex(event)
        );
        return getResponseDTO(event);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEventDTO editEvent(String userId, Long eventId, RequestEventDTO eventDTO) {
        if (eventDTO.getEventDate().isBefore(OffsetDateTime.now().plusHours(2)))
            throw new ValidationException("Event cannot start in less than 2 hours");
        Event event = getEventById(eventId);
        if (! event.getInitiatorId().equals(userId))
            throw new ValidationException("User with id " + userId + " didn't create event with id " + eventId);
        if (event.getState() == EventState.PUBLISHED)
            throw new ValidationException("Event must not be published");

        Event updatedEvent = eventMapper.requestToEvent(eventDTO);
        updatedEvent.setEventId(eventId);

        updatedEvent.setConfirmedRequests(event.getConfirmedRequests());
        updatedEvent.setCreationDate(event.getCreationDate());
        updatedEvent.setInitiatorId(event.getInitiatorId());
        updatedEvent.setState(event.getState());
        updatedEvent.setLikes(event.getLikes());
        updatedEvent.setLikes(event.getDislikes());


        eventRepository.save(updatedEvent);
        eventSearchRepository.save(
                eventMapper.eventToIndex(updatedEvent)
        );
        return getResponseDTO(updatedEvent);
    }

    @Override
    public List<ResponseEventShortDTO> findEvents(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  OffsetDateTime rangeStart,
                                                  OffsetDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  EventSortType sort,
                                                  Integer from,
                                                  Integer size
    ) {

        BoolQuery boolQuery = QueryBuilders.bool()
                .must(m ->m
                        .term(t -> t
                                .field("state")
                                .value("PUBLISHED")
                        )
                )
                .should(m -> {
                    if (!text.isBlank()) {
                        return m.match(q -> q
                                .field("title")
                                .query(text)
                                .fuzziness("AUTO")
                        );
                    } else {
                        return m.matchAll(ma -> ma);
                    }
                }).should(m -> {
                    if (!text.isBlank()) {
                        return m.match(q -> q
                                .field("annotation")
                                .query(text)
                                .fuzziness("AUTO")
                        );
                    } else {
                        return m.matchAll(ma -> ma);
                    }
                }).filter(f -> {
                    if (paid != null) {
                        return f.term(t -> t
                            .field("paid")
                            .value(paid)
                        );
                    } else {
                        return f.matchAll(m -> m);
                    }
                }).filter(f -> {
                    if (rangeStart != null) {
                        return f.range(r -> r
                                .date(d -> d
                                        .field("eventDate")
                                        .gte(rangeStart.toString())
                                )
                        );
                    } else {
                        return f.range(r -> r
                                .date(d -> d
                                        .field("eventDate")
                                        .gte(LocalDateTime.now().toString())
                                )
                        );
                    }
                }).filter(f -> {
                    if (rangeEnd != null) {
                        return f.range(r -> r
                                .date(d -> d
                                        .field("eventDate")
                                        .lte(rangeEnd.toString())
                                )
                        );
                    } else {
                        return f.matchAll(m -> m);
                    }
                }).filter(f -> {
                    if (onlyAvailable) {
                        return f.bool(b -> b
                                .should(s -> s
                                        .term(t -> t
                                                .field("participantLimit").value(0)
                                        )
                                )
                                .should(s -> s.script(sc -> sc
                                        .script(scr -> scr
                                                .source("doc['participantLimit'].size() > 0 && doc['confirmedRequests'].size() > 0 && doc['confirmedRequests'].value < doc['participantLimit'].value")
                                                .lang("painless")
                                        )
                                ))
                                .minimumShouldMatch("1")
                        );
                    } else {
                        return f.matchAll(m -> m);
                    }
                })
                .filter(f -> {
                    if (categories != null && !categories.isEmpty()) {
                        return f.terms(t -> t
                                .field("categoryId")
                                .terms(terms -> terms
                                        .value(categories.stream()
                                                .map(FieldValue::of)
                                                .toList()
                                        )
                                )
                        );
                    } else {
                        return f.matchAll(m -> m);
                    }
                })
//                .minimumShouldMatch("0")
                .build();

        SearchResponse<EventIndex> searchResponse = safeElasticSearch.search(s -> s
                        .index("events")
                        .query(q -> q.bool(boolQuery))
                        .from(from)
                        .size(size)
                , EventIndex.class);

        List<Long> eventIds = searchResponse.hits().hits().stream()
            .map(hit -> Long.valueOf(hit.id()))
            .toList();

        List<Event> events = eventRepository.findAllById(eventIds);
        return events
                .stream()
                .map(this::getResponseShortDTO)
                .toList();
    }

    @Override
    public ResponseEventDTO findEventById(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event with id " + eventId + " not found")
        );
        if (event.getState() != EventState.PUBLISHED)
            throw new EventNotPublishedException("Event with id " + eventId + " not published yet");

        Cache cache = cacheManager.getCache("EventService::getEventViewsById");
        if (cache == null) {
            log.error("Cache EventService::getEventViewsById is null");
        } else {
            cache.put(eventId, getViewsById(eventId) + 1);
        }
        endpointStatisticsService.sendEndpointStatistics(
                RequestEndpointStatsDTO.builder()
                        .app("event-service")
                        .ip(httpServletRequest.getRemoteAddr())
                        .uri(httpServletRequest.getRequestURI())
                        .timestamp(Instant.now())
                        .build()
        );
        return getResponseDTO(event);
    }


    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event with id " + eventId + " not found")
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<ResponseEventShortDTO> findAllUsersEvents(String userId, Integer page, Integer size) {
        Pageable pageRequest = makePageRequest(page, size);
        return eventRepository.findEventsByInitiatorId(userId, pageRequest)
                .stream()
                .map(this::getResponseShortDTO)
                .toList();
    }

    @Override
    public List<ResponseEventShortDTO> findEventsByCategoryId(Long catId) {
        return eventRepository.findEventsByCategoryId(catId)
                .stream()
                .map(eventMapper::eventToShortResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<ResponseEventDTO> findEventsAdmin(List<String> users, List<EventState> states, List<Long> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer page,
                                       Integer size) {
        Pageable pageRequest = makePageRequest(page, size);

        if (rangeStart == null)
            rangeStart = LocalDateTime.now();
        if (rangeEnd == null)
            rangeEnd = LocalDateTime.now().plusYears(1000);

        return eventRepository.searchEventAdmin(users, states, categories, rangeStart, rangeEnd, pageRequest)
                .stream()
                .map(this::getResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void setConfirmedRequestsNumber(ConfirmedRequestsDTO requestsDTO) {
        Event event = getEventById(requestsDTO.getEventId());
        event.setConfirmedRequests(event.getConfirmedRequests() + requestsDTO.getCount());
        eventRepository.save(event);
        eventSearchRepository.save(
                eventMapper.eventToIndex(event)
        );
    }


    @Override
    public List<ResponseEventShortDTO> findEventsByIds(Set<Long> ids) {
        return eventRepository.findAllById(ids).stream()
                .map(eventMapper::eventToShortResponse)
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEventDTO adminEditEvent(Long eventId, RequestUpdateEventAdminDTO requestDTO) {
        Event event = getEventById(eventId);
        if (event.getEventDate().isBefore(OffsetDateTime.now().plusHours(1)))
            throw new ValidationException("Event with id " + eventId + " starts in less than an hour");
        if (! EventState.PENDING.equals(event.getState()))
            throw new ValidationException("Event must be in PENDING state");

        eventMapper.RequestUpdateEventAdminToEvent(event, requestDTO);

        if (EventStateAction.PUBLISH_EVENT.equals(requestDTO.getStateAction())) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(OffsetDateTime.now());
        } else if (EventStateAction.REJECT_EVENT.equals(requestDTO.getStateAction())) {
            event.setState(EventState.CANCELED);
        }

        eventRepository.save(event);
        eventSearchRepository.save(
                eventMapper.eventToIndex(event)
        );
        return getResponseDTO(event);
    }


    private ResponseEventDTO getResponseDTO(Event event) {
        ResponseCategoryDTO category = getCategoryById(event.getCategoryId());
        ResponseUserShortDTO initiator = getUserById(event.getInitiatorId());
        Long views = getViewsById(event.getEventId());

        List<ResponseCommentDTO> comments = commentService.findCommentsByEventId(event.getEventId());

        ResponseEventDTO responseDTO = eventMapper.eventToResponse(event);
        responseDTO.setViews(views);
        responseDTO.setCategory(category);
        responseDTO.setInitiator(initiator);
        responseDTO.setComments(comments);
        return responseDTO;
    }

    private ResponseEventShortDTO getResponseShortDTO(Event event) {
        ResponseCategoryDTO category = getCategoryById(event.getCategoryId());
        ResponseUserShortDTO initiator = getUserById(event.getInitiatorId());
        Long views = getViewsById(event.getEventId());

        ResponseEventShortDTO responseDTO = eventMapper.eventToShortResponse(event);

        responseDTO.setViews(views);
        responseDTO.setCategory(category);
        responseDTO.setInitiator(initiator);
        return responseDTO;
    }

    private Long getViewsById(Long eventId) {
        Cache cache = cacheManager.getCache("EventService::getEventViewsById");
        if (cache == null) {
            log.error("Cache EventService::getEventViewsById is null, requested eventId id: {}", eventId);
            return fetchEventViewsFromDB(eventId);
        }

        Long views = cache.get(eventId, Long.class);
        if (views != null) {
            return views;
        }

        log.debug("Views for event with id {} not found in cache, fetching from statistics service", eventId);
        return fetchEventViewsFromDB(eventId);
    }

    private Long fetchEventViewsFromDB(Long eventId) {
        List<ResponseEndpointStatsDTO> statsById = statisticsServiceClient.getStatsById(eventId);
        return (statsById == null || statsById.isEmpty())
                ? 0
                : statsById.getFirst().getHits();
    }

    private ResponseCategoryDTO getCategoryById(Long id) {
        Cache cache = cacheManager.getCache("CategoryService::findCategoryById");
        if (cache == null) {
            log.error("Cache CategoryService::findCategoryById is null, requested category id: {}", id);
            return categoryServiceClient.getCategoryById(id);
        }

        ResponseCategoryDTO category = cache.get(id, ResponseCategoryDTO.class);
        if (category == null) {
            category = categoryServiceClient.getCategoryById(id);
            log.debug("Category with id {} not found in cache, fetching from service", id);
        }
        return category;
    }

    private ResponseUserShortDTO getUserById(String userId) {
        ResponseUserDTO userDTO;
        Cache cache = cacheManager.getCache("UserService::getUserById");
        if (cache == null) {
            log.error("Cache UserService::getUserById is null, requested userId: {}", userId);
            userDTO = userServiceClient.getUserById(userId);
        } else {
            userDTO = cache.get(userId, ResponseUserDTO.class);
            if (userDTO == null) {
                userDTO = userServiceClient.getUserById(userId);
                log.debug("User with id {} not found in cache, fetching from service", userId);
            }
        }
        return userMapper.responseUserDtoToResponseUserShortDto(userDTO);
    }


}

