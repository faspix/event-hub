package com.faspix.service;

import com.faspix.dto.*;
import com.faspix.entity.Event;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

public interface EventService {

    ResponseEventDTO createEvent(String creatorId, String creatorUsername, RequestEventDTO eventDTO);

    ResponseEventDTO editEvent(String userId, Long eventId, RequestEventDTO eventDTO);

    ResponseEventDTO findEventById(Long eventId, HttpServletRequest httpServletRequest);

    Event getEventById(Long eventId);

    List<ResponseEventShortDTO> findEventsByCategoryId(Long categoryId);

    void setConfirmedRequestsNumber(ConfirmedRequestsDTO requestsDTO);

    ResponseEventDTO adminEditEvent(Long eventId, RequestUpdateEventAdminDTO requestDTO);

    List<ResponseEventShortDTO> findEventsByIds(Set<Long> ids);
}
