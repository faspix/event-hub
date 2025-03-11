package com.faspix.controller;

import com.faspix.dto.*;
import com.faspix.enums.EventState;
import com.faspix.mapper.EventMapper;
import com.faspix.service.EventService;
import com.faspix.utility.EventSortType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEventDTO createEvent(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid RequestEventDTO eventDTO
    ) {
        return eventService.createEvent(jwt.getSubject(), eventDTO);
    }

    @PatchMapping("{eventId}")
    public ResponseEventDTO editEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long eventId,
            @RequestBody RequestEventDTO eventDTO
    ) {
        return eventService.editEvent(jwt.getSubject(), eventId, eventDTO);
    }

    @GetMapping("/users/{userId}")
    public List<ResponseEventShortDTO> findAllUserEvents(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return eventService.findAllUsersEvents(userId, page, size);
    }

    @GetMapping
    public List<ResponseEventShortDTO> findEvents(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") EventSortType sort,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return eventService.findEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, page, size);
    }

    @GetMapping("/admin/search")
    public List<ResponseEventDTO> findEventsByAdmin(
            @RequestParam(required = false) List<String> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return eventService.findEventsAdmin(users, states, categories, rangeStart, rangeEnd, page, size);
    }

    @GetMapping("{eventId}")
    public ResponseEventDTO findEventById(
            @PathVariable Long eventId,
            HttpServletRequest httpServletRequest
    ) {
        return eventService.findEventById(eventId, httpServletRequest);
    }

    @GetMapping("/categories/{catId}")
    public List<ResponseEventShortDTO> findEventsByCategoryId(
            @PathVariable Long catId
    ) {
        return eventService.findEventsByCategoryId(catId);
    }

    @PatchMapping("/admin/{eventId}")
    public ResponseEventDTO adminEditEvent(
            @PathVariable Long eventId,
            @RequestBody RequestUpdateEventAdminDTO requestDTO
    ) {
        return eventService.adminEditEvent(eventId, requestDTO);
    }

    @PostMapping("/batch")
    public List<ResponseEventShortDTO> findUserByIds(
            @RequestBody Set<Long> ids
    ) {
        return eventService.findEventsByIds(ids);
    }

}
