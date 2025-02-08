package com.faspix.service;

import com.faspix.client.UserServiceClient;
import com.faspix.dto.ConfirmedRequestsDTO;
import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.RequestUpdateEventAdminDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.enums.EventStateAction;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.ValidationException;
import com.faspix.mapper.EventMapper;
import com.faspix.repository.EventRepository;
import com.faspix.utility.EventSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;

    private final UserServiceClient userServiceClient;

    private final EventRepository eventRepository;

    @Override
    public Event createEvent(Long creatorId, RequestEventDTO eventDTO) {
        ResponseUserDTO userDTO = userServiceClient.getUserById(creatorId);
        Event event = eventMapper.requestToEvent(eventDTO);

        event.setConfirmedRequests(0);
        event.setCreationDate(OffsetDateTime.now());
        event.setInitiatorId(creatorId);
        event.setState(EventState.PENDING);
        event.setViews(0);

        return eventRepository.save(event);
    }

    @Override
    public Event editEvent(Long userId, Long eventId, RequestEventDTO eventDTO) {
        Event event = findEventById(eventId);
        if (! event.getInitiatorId().equals(userId)) {
            throw new ValidationException("User with id " + userId + " didn't create event with id " + eventId);
        }
        Event updatedEvent = eventMapper.requestToEvent(eventDTO);
        updatedEvent.setEventId(eventId);

        updatedEvent.setConfirmedRequests(event.getConfirmedRequests());
        updatedEvent.setCreationDate(event.getCreationDate());
        updatedEvent.setInitiatorId(event.getInitiatorId());
        updatedEvent.setState(event.getState());
        updatedEvent.setViews(event.getViews());

        return eventRepository.save(updatedEvent);
    }

    @Override
    public List<Event> findEvents(String text, List<Integer> categories, Boolean paid,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                  Boolean onlyAvailable, EventSortType sort, Integer page, Integer size) {
//        Sort sortType = Sort
        Pageable pageRequest = makePageRequest(page, size,
                Sort.by(sort.equals(EventSortType.EVENT_DATE) ? "eventDate" : "views"));
//        sort.equals(EventSortType.EVENT_DATE) ? "eventDate" : "views"

        System.out.println("->>>>>>>>> " + rangeEnd);
        Page<Event> events = eventRepository.searchEvent(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageRequest);

        return events.stream().toList();
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event with id " + eventId + " not found")
        );
    }

    @Override
    public List<Event> findAllUsersEvents(Long userId, Integer page, Integer size) {
        Pageable pageRequest = makePageRequest(page, size);
        return eventRepository.findEventsByInitiatorId(userId, pageRequest)
                .stream()
                .toList();
    }

    @Override
    @Transactional
    public ResponseEntity<HttpStatus> setConfirmedRequestsNumber(ConfirmedRequestsDTO requestsDTO) {
        Event event = findEventById(requestsDTO.getEventId());
        event.setConfirmedRequests(requestsDTO.getCount());
        eventRepository.save(event);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public Event adminEditEvent(Long eventId, RequestUpdateEventAdminDTO requestDTO) {
        Event event = findEventById(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().minusHours(1)))
            throw new ValidationException("Event with id " + eventId + " starts in less than an hour");
        if (! event.getState().equals(EventState.PENDING))
            throw new ValidationException("Event must be in PENDING state");

        updateNotNullFields(requestDTO, event);

        if (EventStateAction.PUBLISH_EVENT.equals(requestDTO.getStateAction())) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(OffsetDateTime.now());
        } else if (EventStateAction.REJECT_EVENT.equals(requestDTO.getStateAction())) {
            event.setState(EventState.CANCELED);
        }

        return eventRepository.save(event);
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


}
