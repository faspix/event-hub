package com.faspix.controller;

import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.mapping.EventMapper;
import com.faspix.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final EventMapper eventMapper;

    @PostMapping("{creatorId}")
    public ResponseEventDTO createEvent(
            @PathVariable Long creatorId,
            @RequestBody @Valid RequestEventDTO eventDTO
    ) {
        return eventMapper.eventToResponse(
                eventService.createEvent(creatorId, eventDTO)
        );
    }


}
