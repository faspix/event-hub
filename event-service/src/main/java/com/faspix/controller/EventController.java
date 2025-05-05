package com.faspix.controller;

import com.faspix.dto.*;
import com.faspix.enums.EventState;
import com.faspix.service.EventService;
import com.faspix.service.SearchService;
import com.faspix.utility.EventSortType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final SearchService searchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEventDTO createEvent(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid RequestEventDTO eventDTO
    ) {
        String username = jwt.getClaim("username");
        return eventService.createEvent(jwt.getSubject(), username, eventDTO);
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
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return searchService.findAllUsersEvents(userId, from, size);
    }

    // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX")
    @GetMapping
    public List<ResponseEventShortDTO> findEvents(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) OffsetDateTime rangeStart,
            @RequestParam(required = false) OffsetDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "NONE") EventSortType sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return searchService.findEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/admin/search")
    public List<ResponseEventDTO> findEventsByAdmin(
            @RequestParam(required = false) List<String> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) OffsetDateTime rangeStart,
            @RequestParam(required = false) OffsetDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return searchService.findEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("{eventId}")
    public ResponseEventDTO findEventById(
            @PathVariable Long eventId,
            HttpServletRequest httpServletRequest
    ) {
        return eventService.findEventById(eventId, httpServletRequest);
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
