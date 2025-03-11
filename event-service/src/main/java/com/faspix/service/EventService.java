package com.faspix.service;

import com.faspix.dto.*;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.utility.EventSortType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {

    ResponseEventDTO createEvent(String creatorId, RequestEventDTO eventDTO);

    ResponseEventDTO editEvent(String userId, Long eventId, RequestEventDTO eventDTO);

    List<ResponseEventShortDTO> findEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                           LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortType sort,
                           Integer page, Integer size);

    ResponseEventDTO findEventById(Long eventId, HttpServletRequest httpServletRequest);

    Event getEventById(Long eventId);

    List<ResponseEventShortDTO> findAllUsersEvents(String userId, Integer page, Integer size);

    List<ResponseEventShortDTO> findEventsByCategoryId(Long categoryId);

    List<ResponseEventDTO> findEventsAdmin(List<String> users, List<EventState> states, List<Long> categories,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer page, Integer size);

    void setConfirmedRequestsNumber(ConfirmedRequestsDTO requestsDTO);

    ResponseEventDTO adminEditEvent(Long eventId, RequestUpdateEventAdminDTO requestDTO);

    void setViews(SetViewsDTO viewsDTO);

    List<ResponseEventShortDTO> findEventsByIds(Set<Long> ids);
}
