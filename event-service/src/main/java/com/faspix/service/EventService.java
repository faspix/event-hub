package com.faspix.service;

import com.faspix.domain.entity.Event;
import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.RequestUpdateEventAdminDTO;
import com.faspix.shared.dto.ConfirmedRequestsDTO;
import com.faspix.shared.dto.ResponseEventDTO;
import com.faspix.shared.dto.UpdateCategoryNameDTO;
import com.faspix.shared.dto.UpdateUsernameDTO;

public interface EventService {

    ResponseEventDTO createEvent(String creatorId, String creatorUsername, RequestEventDTO eventDTO);

    ResponseEventDTO editEvent(String userId, Long eventId, RequestEventDTO eventDTO);

    Event getEventById(Long eventId);

    void setConfirmedRequestsNumber(ConfirmedRequestsDTO requestsDTO);

    void updateInitiatorUsername(UpdateUsernameDTO dto);

    void updateCategoryName(UpdateCategoryNameDTO dto);

    ResponseEventDTO adminEditEvent(Long eventId, RequestUpdateEventAdminDTO requestDTO);

}
