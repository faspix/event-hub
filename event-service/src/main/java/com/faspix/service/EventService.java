package com.faspix.service;

import com.faspix.dto.ConfirmedRequestsDTO;
import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.RequestUpdateEventAdminDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.utility.EventSortType;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public interface EventService {

    Event createEvent(Long creatorId, RequestEventDTO eventDTO);

    Event editEvent(Long userId, Long eventId, RequestEventDTO eventDTO);

    List<Event> findEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                           LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortType sort,
                           Integer page, Integer size);

    Event findEventById(Long eventId);

    List<Event> findAllUsersEvents(Long userId, Integer page, Integer size);

    List<Event> findEventsByCategoryId(Long catId);

    List<Event> findEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer page, Integer size);

    ResponseEntity<HttpStatus> setConfirmedRequestsNumber(ConfirmedRequestsDTO requestsDTO);

    Event adminEditEvent(Long eventId, RequestUpdateEventAdminDTO requestDTO);
}
