package com.faspix.controller;

import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.RequestUpdateEventAdminDTO;
import com.faspix.service.EventService;
import com.faspix.shared.dto.ResponseEventDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/admin/{eventId}")
    public ResponseEventDTO adminEditEvent(
            @PathVariable Long eventId,
            @RequestBody RequestUpdateEventAdminDTO requestDTO
    ) {
        return eventService.adminEditEvent(eventId, requestDTO);
    }

}
