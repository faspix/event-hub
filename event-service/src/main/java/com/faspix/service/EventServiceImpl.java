package com.faspix.service;

import com.faspix.client.CategoryServiceClient;
import com.faspix.client.UserServiceClient;
import com.faspix.dto.*;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.enums.EventStateAction;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.ValidationException;
import com.faspix.mapper.EventMapper;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.EventRepository;
import com.faspix.utility.EventSortType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;

    private final UserServiceClient userServiceClient;

    private final CategoryServiceClient categoryServiceClient;

    private final EventRepository eventRepository;

    private final EndpointStatisticsService endpointStatisticsService;

    private final UserMapper userMapper;

    private final CommentService commentService;

    private final CacheManager cacheManager;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEventDTO createEvent(String creatorId, RequestEventDTO eventDTO) {
        if (eventDTO.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
            throw new ValidationException("Event cannot start in less than 2 hours");
        userServiceClient.getUserById(creatorId);
        Event event = eventMapper.requestToEvent(eventDTO);

        event.setConfirmedRequests(0);
        event.setCreationDate(OffsetDateTime.now());
        event.setInitiatorId(creatorId);
        event.setState(EventState.PENDING);
        event.setViews(0L);
        event.setLikes(0);

        eventRepository.save(event);
        return getResponseDTO(event);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEventDTO editEvent(String userId, Long eventId, RequestEventDTO eventDTO) {
        if (eventDTO.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))
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
        updatedEvent.setViews(event.getViews());

        eventRepository.save(updatedEvent);
        return getResponseDTO(updatedEvent);
    }

    @Override
    public List<ResponseEventShortDTO> findEvents(String text, List<Long> categories, Boolean paid,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                  Boolean onlyAvailable, EventSortType sort, Integer page, Integer size) {
        Pageable pageRequest;
        if (sort.equals(EventSortType.EVENT_DATE))
            pageRequest = makePageRequest(page, size, Sort.by("eventDate"));
        else
            pageRequest = makePageRequest(page, size, Sort.by("views").descending());

        if (rangeStart == null)
            rangeStart = LocalDateTime.now();
        if (rangeEnd == null)
            rangeEnd = LocalDateTime.now().plusYears(1000);

        Page<Event> events = eventRepository.searchEvent(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, pageRequest);
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
            throw new EventNotFoundException("Event with id " + eventId + " not published yet");
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
    }

    @Override
    @Transactional
    public void setViews(SetViewsDTO viewsDTO) {
        Event event = getEventById(viewsDTO.getEventId());
        event.setViews(event.getViews() + viewsDTO.getCount());
        eventRepository.save(event);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEventDTO adminEditEvent(Long eventId, RequestUpdateEventAdminDTO requestDTO) {
        Event event = getEventById(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)))
            throw new ValidationException("Event with id " + eventId + " starts in less than an hour");
        if (! EventState.PENDING.equals(event.getState()))
            throw new ValidationException("Event must be in PENDING state");

        updateNotNullFields(requestDTO, event);

        if (EventStateAction.PUBLISH_EVENT.equals(requestDTO.getStateAction())) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(OffsetDateTime.now());
        } else if (EventStateAction.REJECT_EVENT.equals(requestDTO.getStateAction())) {
            event.setState(EventState.CANCELED);
        }

        eventRepository.save(event);

        return getResponseDTO(event);
    }

    private static void updateNotNullFields(RequestUpdateEventAdminDTO requestDTO, Event event) {
        if (requestDTO.getAnnotation() != null)
            event.setAnnotation(requestDTO.getAnnotation());
        if (requestDTO.getCategoryId() != null)
            event.setCategoryId(requestDTO.getCategoryId());
        if (requestDTO.getDescription() != null)
            event.setDescription(requestDTO.getDescription());
        if (requestDTO.getEventDate() != null)
            event.setEventDate(requestDTO.getEventDate());
        if (requestDTO.getLocation() != null)
            event.setLocation(requestDTO.getLocation());
        if (requestDTO.getPaid() != null)
            event.setPaid(requestDTO.getPaid());
        if (requestDTO.getParticipantLimit() != null)
            event.setParticipantLimit(requestDTO.getParticipantLimit());
        if (requestDTO.getRequestModeration() != null)
            event.setRequestModeration(requestDTO.getRequestModeration());
        if (requestDTO.getTitle() != null)
            event.setTitle(requestDTO.getTitle());
    }


    private ResponseEventDTO getResponseDTO(Event event) {
        ResponseCategoryDTO category = getCategoryById(event.getCategoryId());
        ResponseUserShortDTO initiator = getInitiatorById(event.getInitiatorId());

        List<ResponseCommentDTO> comments = commentService.findCommentsByEventId(event.getEventId());

        ResponseEventDTO responseDTO = eventMapper.eventToResponse(event);
        responseDTO.setCategory(category);
        responseDTO.setInitiator(initiator);
        responseDTO.setComments(comments);
        return responseDTO;
    }

    private ResponseEventShortDTO getResponseShortDTO(Event event) {
        ResponseCategoryDTO category = getCategoryById(event.getCategoryId());
        ResponseUserShortDTO initiator = getInitiatorById(event.getInitiatorId());

        ResponseEventShortDTO responseDTO = eventMapper.eventToShortResponse(event);

        responseDTO.setCategory(category);
        responseDTO.setInitiator(initiator);
        return responseDTO;
    }

    private ResponseCategoryDTO getCategoryById(Long id) {
        Cache cache = cacheManager.getCache("CategoryService::findCategoryById");
        if (cache == null) {
            log.warn("Cache CategoryService::findCategoryById is null, requested category id: {}", id);
            return categoryServiceClient.getCategoryById(id);
        }

        ResponseCategoryDTO category = cache.get(id, ResponseCategoryDTO.class);
        if (category == null) {
            category = categoryServiceClient.getCategoryById(id);
            log.debug("Category with id {} not found in cache, fetching from service", id);
        }
        return category;
    }

    private ResponseUserShortDTO getInitiatorById(String id) {
        return userMapper.responseUserDtoToResponseUserShortDto(
                userServiceClient.getUserById(id)
        );
    }

}
