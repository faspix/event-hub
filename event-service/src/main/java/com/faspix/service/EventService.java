package com.faspix.service;

import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.entity.Event;

public interface EventService {

    Event createEvent(Long creatorId, RequestEventDTO eventDTO);

}
