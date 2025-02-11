package com.faspix.controller;

import com.faspix.dto.RequestParticipationRequestDTO;
import com.faspix.dto.ResponseParticipationRequestDTO;
import com.faspix.mapper.RequestMapper;
import com.faspix.service.RequestService;
import jakarta.ws.rs.HeaderParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    private final RequestMapper requestMapper;


    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseParticipationRequestDTO createRequest(
            @RequestHeader("X-User-Id") Long requesterId,
            @PathVariable Long eventId
    ) {
        return requestService.createRequest(requesterId, eventId);
    }

    @PatchMapping("/events/{eventId}/cancel")
    public ResponseParticipationRequestDTO cancelRequest(
            @RequestHeader("X-User-Id") Long requesterId,
            @PathVariable Long eventId
    ) {
        return requestService.cancelRequest(requesterId, eventId);
    }

    @GetMapping("/events/{eventId}")
    public List<ResponseParticipationRequestDTO> getRequestsToMyEvent(
            @RequestHeader("X-User-Id") Long requesterId,
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return requestService.getRequestsToMyEvent(requesterId, eventId, page, size);
    }

    @PatchMapping("/events/{eventId}")
    public List<ResponseParticipationRequestDTO> setRequestsStatus(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long eventId,
            @RequestBody RequestParticipationRequestDTO requestDTO
    ) {
        return requestService.setRequestsStatus(userId, eventId, requestDTO);
    }

    @GetMapping("/users")
    public List<ResponseParticipationRequestDTO> getUsersRequests(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size

    ) {
        return requestService.getUsersRequests(userId, page, size);
    }


}
