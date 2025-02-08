package com.faspix.controller;

import com.faspix.dto.ResponseParticipationRequestDTO;
import com.faspix.mapper.RequestMapper;
import com.faspix.service.RequestService;
import jakarta.ws.rs.HeaderParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    private final RequestMapper requestMapper;


    @PostMapping("/events/{eventId}")
    public ResponseParticipationRequestDTO createRequest(
            @RequestHeader("X-User-Id") Long requesterId,
            @PathVariable Long eventId
    ) {
        return requestMapper.participationRequestToResponse(
                requestService.createRequest(requesterId, eventId)
        );
    }

    @PatchMapping("/events/{eventId}")
    public ResponseParticipationRequestDTO cancelRequest(
            @HeaderParam("X-User-Id") Long requesterId,
            @PathVariable Long eventId
    ) {
        return requestMapper.participationRequestToResponse(
                requestService.cancelRequest(requesterId, eventId)
        );
    }

    @GetMapping("/events/{eventId}")
    public ResponseParticipationRequestDTO getRequestsToEvent(
            @HeaderParam("X-User-Id") Long requesterId,
            @PathVariable Long eventId
    ) {
        return requestMapper.participationRequestToResponse(
                requestService.getRequestsToEvent(requesterId, eventId)
        );
    }

    @GetMapping("/users/{userId}")
    public List<ResponseParticipationRequestDTO> getUsersRequests(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size

    ) {
        return requestService.getUsersRequests(userId, page, size)
                .stream()
                .map(requestMapper::participationRequestToResponse)
                .toList();
    }

//    @PatchMapping("")
//    public

}
