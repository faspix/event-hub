package com.faspix.controller;

import com.faspix.dto.*;
import com.faspix.mapper.EventMapper;
import com.faspix.service.EventService;
import com.faspix.utility.EventSortType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final EventMapper eventMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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

    @GetMapping("/users/{userId}")
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
            @RequestParam(defaultValue = "") String text,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
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

    @GetMapping("/categories/{catId}")
    public List<ResponseEventShortDTO> findEventsByCategoryId(
            @PathVariable Long catId
    ) {
        return eventService.findEventsByCategoryId(catId)
                .stream()
                .map(eventMapper::eventToShortResponse)
                .toList();
    }

    @PostMapping("/requests")
    public ResponseEntity<HttpStatus> setConfirmedRequestsNumber(
            @RequestBody ConfirmedRequestsDTO requestsDTO
    ) {
        return eventService.setConfirmedRequestsNumber(requestsDTO);
    }

    @PatchMapping("/admin/{eventId}")
    public ResponseEventDTO adminEditEvent(
            @PathVariable Long eventId,
            @RequestBody RequestUpdateEventAdminDTO requestDTO
    ) {
        return eventMapper.eventToResponse(
                eventService.adminEditEvent(eventId, requestDTO)
        );
    }

}
