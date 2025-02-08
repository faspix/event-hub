package com.faspix.service;

import com.faspix.client.EventServiceClient;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.entity.Request;
import com.faspix.enums.EventState;
import com.faspix.enums.ParticipationRequestState;
import com.faspix.exception.RequestNotFountException;
import com.faspix.exception.ValidationException;
import com.faspix.mapper.RequestMapper;
import com.faspix.repository.RequestRepository;
import com.faspix.utility.PageRequestMaker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestMapper requestMapper;

    private final RequestRepository requestRepository;

    private final EventServiceClient eventServiceClient;

    @Override
    public Request createRequest(Long requesterId, Long eventId) {
        if (requestRepository.findRequestByRequesterIdAndEventId(requesterId, eventId) != null)
            throw new ValidationException("User with id " + requesterId +
                    " already leave a request to participate in event with id " + eventId);
        ResponseEventDTO event = eventServiceClient.findEventById(eventId);
        if (event.getInitiator().getUserId().equals(requesterId))
            throw new ValidationException("Event initiator cannot leave a request to participate in his event");
//        if (! event.getState().equals(EventState.PUBLISHED))
//            throw new ValidationException("You cannot participate in an unpublished event");
        if (event.getConfirmedRequests() >= event.getParticipantLimit())
            throw new ValidationException("The event has reached the limit of requests for participation");
        Request request = new Request(eventId, requesterId, OffsetDateTime.now());

        if (event.getRequestModeration()) {
            request.setState(ParticipationRequestState.PENDING);
        } else {
            request.setState(ParticipationRequestState.ACCEPTED);
        }

        return requestRepository.save(request);
    }

    @Override
    public Request cancelRequest(Long requesterId, Long eventId) {
        Request request = requestRepository.findRequestByRequesterIdAndEventId(requesterId, eventId);
        if (request == null) {
            throw new RequestNotFountException("User with id " + requesterId +
                    " didn't leave a request to participate in event with id " + eventId);
        }
        requestRepository.delete(request);
        return request;
    }

    @Override
    public List<Request> getRequestsToMyEvent(Long requesterId, Long eventId, Integer page, Integer size) {
        if (! eventServiceClient.findEventById(eventId).getInitiator().getUserId().equals(requesterId)) {
            throw new ValidationException("User with id " + requesterId + " doesn't own event with id " + eventId);
        }
        Pageable pageRequest = makePageRequest(page, size);
        return requestRepository.findRequestsByEventId(eventId, pageRequest);
    }

    @Override
    public Request findRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFountException("Request with id " + requestId + " not found")
        );
    }

    @Override
    public Request setRequestStatus(Long userId, Long requesterId, Long eventId) {
        return null;
    }

    @Override
    public List<Request> getUsersRequests(Long requesterId, Integer page, Integer size) {
        Pageable pageable = makePageRequest(page, size);
        return requestRepository.findRequestsByRequesterId(requesterId, pageable);
    }
}
