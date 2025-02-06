package com.faspix.controller;

import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.mapper.EventMapper;
import com.faspix.service.EventService;
import com.faspix.utility.EventSortType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final EventMapper eventMapper;

    @PostMapping()
    public ResponseEventDTO createEvent(
            @RequestHeader(value = "X-User-Id") Long creatorId,
            @RequestBody @Valid RequestEventDTO eventDTO
    ) {
        return eventMapper.eventToResponse(
                eventService.createEvent(creatorId, eventDTO)
        );
    }

    @PatchMapping("{eventId}")
    public ResponseEventDTO editEvent(
            @RequestHeader(value = "X-User-Id") Long userId,
            @PathVariable Long eventId,
            @RequestBody RequestEventDTO eventDTO
    ) {
        return eventMapper.eventToResponse(
                eventService.editEvent(userId, eventId, eventDTO)
        );
    }

    @GetMapping("/user/{userId}")
    public List<ResponseEventShortDTO> findAllUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return eventService.findAllUsersEvents(userId, page, size)
                .stream()
                .map(eventMapper::eventToShortResponse)
                .toList();
    }

    @GetMapping
    public List<ResponseEventShortDTO> findEvents(
            @RequestParam String text,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) OffsetDateTime rangeStart,
            @RequestParam(required = false) OffsetDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") EventSortType sort,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return eventService.findEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, page, size)
                        .stream()
                        .map(eventMapper::eventToShortResponse)
                        .toList();
    }

    @GetMapping("{eventId}")
    public ResponseEventDTO findEventById(
            @PathVariable Long eventId
    ) {
        return eventMapper.eventToResponse(
                eventService.findEventById(eventId)
        );
    }




}
