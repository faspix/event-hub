package com.faspix.service;

import com.faspix.dto.RequestEventDTO;
import com.faspix.entity.Event;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {


    @Override
    public Event createEvent(Long creatorId, RequestEventDTO eventDTO) {
        return null;
    }



}
