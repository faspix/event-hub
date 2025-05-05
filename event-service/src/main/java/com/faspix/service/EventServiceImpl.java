package com.faspix.service;

import com.faspix.client.CategoryServiceClient;
import com.faspix.dto.*;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.enums.EventStateAction;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.EventNotPublishedException;
import com.faspix.exception.ValidationException;
import com.faspix.mapper.EventMapper;
import com.faspix.repository.EventRepository;
import com.faspix.repository.EventSearchRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;

    private final CategoryServiceClient categoryServiceClient;

    private final EventRepository eventRepository;

    private final EndpointStatisticsService endpointStatisticsService;

    private final EventViewService eventViewService;

    private final CacheManager cacheManager;

    private final EventSearchRepository eventSearchRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEventDTO createEvent(String creatorId, String creatorUsername, RequestEventDTO eventDTO) {
        if (eventDTO.getEventDate().isBefore(OffsetDateTime.now().plusHours(2)))
            throw new ValidationException("Event cannot start in less than 2 hours");
        Event event = eventMapper.requestToEvent(eventDTO);

        event.setConfirmedRequests(0);
        event.setInitiatorId(creatorId);
        event.setInitiatorUsername(creatorUsername);
        event.setCategoryName(getCategoryById(event.getCategoryId()).getName());
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
        updatedEvent.setInitiatorUsername(event.getInitiatorUsername());
        updatedEvent.setState(event.getState());
        updatedEvent.setLikes(event.getLikes());
        updatedEvent.setDislikes(event.getDislikes());

        if (!event.getCategoryId().equals(updatedEvent.getCategoryId())) {
            updatedEvent.setCategoryName(getCategoryById(
                    updatedEvent.getCategoryId()
            ).getName());
        } else {
            updatedEvent.setCategoryName(event.getCategoryName());
        }

        eventRepository.save(updatedEvent);
        eventSearchRepository.save(
                eventMapper.eventToIndex(updatedEvent)
        );
        return getResponseDTO(updatedEvent);
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
            cache.put(eventId, eventViewService.getViewsByEventId(eventId) + 1);
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
    public List<ResponseEventShortDTO> findEventsByCategoryId(Long catId) {
        return eventRepository.findEventsByCategoryId(catId)
                .stream()
                .map(eventMapper::eventToShortResponse)
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
    @Transactional
    public void updateInitiatorUsername(UpdateUsernameDTO dto) {
        log.debug("Update initiator username: {}", dto);
        eventRepository.updateEventInitiatorName(dto.getUserId(), dto.getUsername());
    }

    @Override
    @Transactional
    public void updateCategoryName(UpdateCategoryNameDTO dto) {
        log.debug("Update category name: {}", dto);
        eventRepository.updateCategoryName(dto.getCategoryId(), dto.getCategoryName());
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

        if (requestDTO.getCategoryId() != null && !event.getCategoryId().equals(requestDTO.getCategoryId())) {
            event.setCategoryName(getCategoryById(
                    requestDTO.getCategoryId()
            ).getName());
        }

        eventMapper.RequestUpdateEventAdminToEvent(event, requestDTO);

        if (EventStateAction.PUBLISH_EVENT.equals(requestDTO.getStateAction())) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedAt(OffsetDateTime.now());
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
        Long views = eventViewService.getViewsByEventId(event.getEventId());
        ResponseEventDTO responseDTO = eventMapper.eventToResponse(event);

        responseDTO.setViews(views);
        return responseDTO;
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

}

