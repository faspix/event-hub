package com.faspix.controller;

import com.faspix.dto.*;
import com.faspix.enums.EventState;
import com.faspix.service.SearchService;
import com.faspix.utility.EventSortType;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventSearchController {

    private final SearchService searchService;


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
        return searchService.findEventById(eventId, httpServletRequest);
    }

    @PostMapping("/batch")
    @Hidden
    public List<ResponseEventShortDTO> findEventsByIds(
            @RequestBody Set<Long> ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return searchService.findEventsByIds(ids, from, size);
    }


}
