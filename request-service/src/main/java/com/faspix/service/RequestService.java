package com.faspix.service;

import com.faspix.dto.RequestParticipationRequestDTO;
import com.faspix.dto.ResponseParticipationRequestDTO;
import com.faspix.entity.Request;

import java.util.List;

public interface RequestService {

    ResponseParticipationRequestDTO createRequest(Long requesterId, Long eventId);

    ResponseParticipationRequestDTO cancelRequest(Long requesterId, Long eventId);

    List<ResponseParticipationRequestDTO> getRequestsToMyEvent(Long requesterId, Long eventId, Integer page, Integer size);

    ResponseParticipationRequestDTO findRequestById(Long requestId);

    List<ResponseParticipationRequestDTO> setRequestsStatus(Long userId, Long eventId, RequestParticipationRequestDTO requestDTO);

    List<ResponseParticipationRequestDTO> getUsersRequests(Long requesterId, Integer page, Integer size);

}
