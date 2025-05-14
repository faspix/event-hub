package com.faspix.service;

import com.faspix.dto.RequestParticipationRequestDTO;
import com.faspix.dto.ResponseParticipationRequestDTO;

import java.util.List;

public interface RequestService {

    ResponseParticipationRequestDTO createRequest(String requesterId, Long eventId);

    ResponseParticipationRequestDTO cancelRequest(String requesterId, Long eventId);

    List<ResponseParticipationRequestDTO> getRequestsToMyEvent(String requesterId, Long eventId, Integer from, Integer size);

    List<ResponseParticipationRequestDTO> setRequestsStatus(String userId, Long eventId, RequestParticipationRequestDTO requestDTO);

    List<ResponseParticipationRequestDTO> getUsersRequests(String requesterId, Integer from, Integer size);

}
