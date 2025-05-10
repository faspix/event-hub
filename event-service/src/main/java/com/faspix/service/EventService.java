package com.faspix.service;

import com.faspix.dto.*;
import com.faspix.entity.Event;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

public interface EventService {

    ResponseEventDTO createEvent(String creatorId, String creatorUsername, RequestEventDTO eventDTO);

    ResponseEventDTO editEvent(String userId, Long eventId, RequestEventDTO eventDTO);

    Event getEventById(Long eventId);

    void setConfirmedRequestsNumber(ConfirmedRequestsDTO requestsDTO);

    void updateInitiatorUsername(UpdateUsernameDTO dto);

    void updateCategoryName(UpdateCategoryNameDTO dto);

    ResponseEventDTO adminEditEvent(Long eventId, RequestUpdateEventAdminDTO requestDTO);

}
