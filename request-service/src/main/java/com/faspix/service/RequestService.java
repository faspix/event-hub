package com.faspix.service;

import com.faspix.dto.RequestParticipationRequestDTO;
import com.faspix.entity.Request;

import java.util.List;

public interface RequestService {

    Request createRequest(Long requesterId, Long eventId);

    Request cancelRequest(Long requesterId, Long eventId);

    List<Request> getRequestsToMyEvent(Long requesterId, Long eventId, Integer page, Integer size);

    Request findRequestById(Long requestId);

    List<Request> setRequestsStatus(Long userId, Long eventId, RequestParticipationRequestDTO requestDTO);

    List<Request> getUsersRequests(Long requesterId, Integer page, Integer size);

}
