package com.faspix.service;

import com.faspix.entity.Request;

import java.util.List;

public interface RequestService {

    Request createRequest(Long requesterId, Long eventId);

    Request cancelRequest(Long requesterId, Long eventId);

    Request getRequestsToEvent(Long requesterId, Long eventId);

    Request findRequestById(Long requestId);

    Request setRequestStatus(Long userId, Long requesterId, Long eventId);

    List<Request> getUsersRequests(Long requesterId, Integer page, Integer size);

}
