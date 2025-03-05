package com.faspix.controller;

import com.faspix.service.EventReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventReactionController {

    private final EventReactionService eventReactionService;

    @PostMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void likeEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long eventId
    ) {
        eventReactionService.likeEvent(jwt.getSubject(), eventId);
    }


    @PostMapping("/{eventId}/dislike")
    @ResponseStatus(HttpStatus.CREATED)
    public void dislikeEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long eventId
    ) {
        eventReactionService.dislikeEvent(jwt.getSubject(), eventId);
    }

    @DeleteMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikeEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long eventId
    ) {
        eventReactionService.removeLikeEvent(jwt.getSubject(), eventId);
    }


    @DeleteMapping("/{eventId}/dislike")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDislikeEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long eventId
    ) {
        eventReactionService.removeDislikeEvent(jwt.getSubject(), eventId);
    }


}
