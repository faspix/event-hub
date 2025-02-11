package com.faspix.service;

import com.faspix.client.EventServiceClient;
import com.faspix.dto.*;
import com.faspix.entity.Request;
import com.faspix.enums.EventState;
import com.faspix.enums.ParticipationRequestState;
import com.faspix.exception.RequestNotFountException;
import com.faspix.exception.ValidationException;
import com.faspix.mapper.RequestMapper;
import com.faspix.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestMapper requestMapper;

    private final RequestRepository requestRepository;

    private final EventServiceClient eventServiceClient;

    @Override
    @Transactional // TODO: ErrorResponse: Request must have status PENDING
    public ResponseParticipationRequestDTO createRequest(Long requesterId, Long eventId) {
        if (requestRepository.findRequestByRequesterIdAndEventId(requesterId, eventId) != null)
            throw new ValidationException("User with id " + requesterId +
                    " already leave a request to participate in event with id " + eventId);
        ResponseEventDTO event = eventServiceClient.findEventById(eventId);
        if (event.getInitiator().getUserId().equals(requesterId))
            throw new ValidationException("Event initiator cannot leave a request to participate in his event");
        if (! EventState.PUBLISHED.equals(event.getState()))
            throw new ValidationException("User cannot participate in an unpublished event");
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit())
            throw new ValidationException("The event has reached the limit of requests for participation");
        Request request = new Request(eventId, requesterId, OffsetDateTime.now());

        if (event.getRequestModeration()) {
            request.setState(ParticipationRequestState.PENDING);
        } else {
            request.setState(ParticipationRequestState.CONFIRMED);
            eventServiceClient.setConfirmedRequestsNumber(
                    new ConfirmedRequestsDTO(eventId, event.getConfirmedRequests() + 1)
            );
        }

        return requestMapper.participationRequestToResponse(
                requestRepository.save(request)
        );
    }

    @Override
    @Transactional
    public ResponseParticipationRequestDTO cancelRequest(Long requesterId, Long eventId) {
        Request request = requestRepository.findRequestByRequesterIdAndEventId(requesterId, eventId);
        if (request == null) {
            throw new RequestNotFountException("User with id " + requesterId +
                    " didn't leave a request to participate in event with id " + eventId);
        }
        requestRepository.delete(request);
        return requestMapper.participationRequestToResponse(request);
    }

    @Override
    public List<ResponseParticipationRequestDTO> getRequestsToMyEvent(Long requesterId, Long eventId, Integer page, Integer size) {
        validateOwnership(requesterId, eventId, eventServiceClient.findEventById(eventId));
        Pageable pageRequest = makePageRequest(page, size);
        return requestRepository.findRequestsByEventId(eventId, pageRequest)
                .stream()
                .map(requestMapper::participationRequestToResponse)
                .toList();
    }

    @Override
    public ResponseParticipationRequestDTO findRequestById(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFountException("Request with id " + requestId + " not found")
        );
        return requestMapper.participationRequestToResponse(request);
    }

    @Override
    @Transactional
    public List<ResponseParticipationRequestDTO> setRequestsStatus(Long userId, Long eventId, RequestParticipationRequestDTO requestDTO) {
        ResponseEventDTO eventDTO = eventServiceClient.findEventById(eventId);
        validateOwnership(userId, eventId, eventDTO);

        int limit = getAvailableRequestsLimit(eventDTO);

        int counter = 0;
        List<Request> requests = new ArrayList<>();
        List<Request> requestsList = requestRepository.findAllById(requestDTO.getRequestIds());
        for (Request request : requestsList) {
            if (ParticipationRequestState.PENDING.equals(request.getState())) {
                if (requestDTO.getStatus() == ParticipationRequestState.CONFIRMED && counter < limit) {
                    request.setState(ParticipationRequestState.CONFIRMED);
                    counter++;
                } else {
                    request.setState(ParticipationRequestState.REJECTED);
                }
                requests.add(request);
            }
        }
        requestRepository.saveAllAndFlush(requests);

        rejectPendingRequests(eventId, counter, limit);

        // TODO: AMQP
        Integer confirmedRequests = eventDTO.getConfirmedRequests() + counter;
        eventServiceClient.setConfirmedRequestsNumber(new ConfirmedRequestsDTO(eventId, confirmedRequests));

        return requests.stream()
                .map(requestMapper::participationRequestToResponse)
                .toList();
    }

    @Override
    public List<ResponseParticipationRequestDTO> getUsersRequests(Long requesterId, Integer page, Integer size) {
        Pageable pageable = makePageRequest(page, size);
        return requestRepository.findRequestsByRequesterId(requesterId, pageable)
                .stream()
                .map(requestMapper::participationRequestToResponse)
                .toList();
    }


    private static void validateOwnership(Long userId, Long eventId, ResponseEventDTO eventDTO) {
        if (! eventDTO.getInitiator().getUserId().equals(userId)) {
            throw new ValidationException("User with id " + userId + " doesn't own event with id " + eventId);
        }
    }

    private int getAvailableRequestsLimit(ResponseEventDTO eventDTO) {
        int limit = 0;
        Long eventId = eventDTO.getEventId();
        int participantLimit = eventDTO.getParticipantLimit();
        if (participantLimit != 0) {
            int acceptedRequest = requestRepository.getAcceptedEventsCount(eventId);
            limit = participantLimit - acceptedRequest;
            if (limit <= 0) {
                throw new ValidationException("Request limit to event with id " + eventId + " has been reached");
            }
        } else {
            limit = Integer.MAX_VALUE;
        }
        return limit;
    }

    private void rejectPendingRequests(Long eventId, int counter, int limit) {
        if (counter >= limit) {
            List<Request> remainingPendingRequests = requestRepository
                    .findRequestsByEventIdAndState(eventId, ParticipationRequestState.PENDING);
            if (! remainingPendingRequests.isEmpty()) {
                remainingPendingRequests.forEach(r -> r.setState(ParticipationRequestState.REJECTED));
                requestRepository.saveAll(remainingPendingRequests);
            }
        }
    }

}
