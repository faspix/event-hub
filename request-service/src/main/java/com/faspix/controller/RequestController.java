package com.faspix.controller;

import com.faspix.dto.RequestParticipationRequestDTO;
import com.faspix.dto.ResponseParticipationRequestDTO;
import com.faspix.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;


    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseParticipationRequestDTO createRequest(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long eventId
    ) {
        return requestService.createRequest(jwt.getSubject(), eventId);
    }

    @PatchMapping("/events/{eventId}/cancel")
    public ResponseParticipationRequestDTO cancelRequest(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long eventId
    ) {
        return requestService.cancelRequest(jwt.getSubject(), eventId);
    }

    @GetMapping("/events/{eventId}")
    public List<ResponseParticipationRequestDTO> getRequestsToMyEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return requestService.getRequestsToMyEvent(jwt.getSubject(), eventId, page, size);
    }

    @PatchMapping("/events/{eventId}")
    public List<ResponseParticipationRequestDTO> setRequestsStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long eventId,
            @RequestBody RequestParticipationRequestDTO requestDTO
    ) {
        return requestService.setRequestsStatus(jwt.getSubject(), eventId, requestDTO);
    }

    @GetMapping("/users")
    public List<ResponseParticipationRequestDTO> getUsersRequests(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size

    ) {
        return requestService.getUsersRequests(jwt.getSubject(), page, size);
    }


}
