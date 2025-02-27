package service;

import com.faspix.client.EventServiceClient;
import com.faspix.dto.RequestParticipationRequestDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseParticipationRequestDTO;
import com.faspix.entity.Request;
import com.faspix.enums.EventState;
import com.faspix.enums.ParticipationRequestState;
import com.faspix.exception.RequestNotFountException;
import com.faspix.exception.ValidationException;
import com.faspix.mapper.RequestMapper;
import com.faspix.repository.RequestRepository;
import com.faspix.service.ConfirmedRequestService;
import com.faspix.service.RequestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static utility.EventFactory.*;
import static utility.RequestFactory.*;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private EventServiceClient eventServiceClient;

    @Mock
    private ConfirmedRequestService confirmedRequestService;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Spy
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    @Test
    public void createRequestTest_NoModeration_Success() {
        ResponseEventDTO responseEventDTO = makeResponseEventTest();
        responseEventDTO.setRequestModeration(false);
        Request responseParticipationRequest = makeRequest();
        responseEventDTO.setState(EventState.PUBLISHED);
        responseEventDTO.setParticipantLimit(20);
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(responseEventDTO);
        when(requestRepository.save(any()))
                .thenReturn(responseParticipationRequest);

        ResponseParticipationRequestDTO request = requestService.createRequest("2", 1L);

        assertThat(request.getRequesterId(), equalTo(responseParticipationRequest.getRequesterId()));
        assertThat(request.getState(), equalTo(responseParticipationRequest.getState()));
        assertThat(request.getCreationDate(), equalTo(responseParticipationRequest.getCreationDate()));
        assertThat(request.getEventId(), equalTo(responseParticipationRequest.getEventId()));

        verify(confirmedRequestService, times(1)).sendConfirmedRequestMsg(any());
        verify(requestRepository, times(1)).save(any());

    }


    @Test
    public void createRequestTest_Moderation_Sunccess() {
        ResponseEventDTO responseEventDTO = makeResponseEventTest();
        responseEventDTO.setRequestModeration(false);
        Request responseParticipationRequest = makeRequest();
        responseEventDTO.setState(EventState.PUBLISHED);
        responseEventDTO.setParticipantLimit(20);
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(responseEventDTO);
        when(requestRepository.save(any()))
                .thenReturn(responseParticipationRequest);

        ResponseParticipationRequestDTO request = requestService.createRequest("2", 1L);

        assertThat(request.getRequesterId(), equalTo(responseParticipationRequest.getRequesterId()));
        assertThat(request.getState(), equalTo(responseParticipationRequest.getState()));
        assertThat(request.getCreationDate(), equalTo(responseParticipationRequest.getCreationDate()));
        assertThat(request.getEventId(), equalTo(responseParticipationRequest.getEventId()));

        verify(confirmedRequestService, times(1)).sendConfirmedRequestMsg(any());
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    public void createRequestTest_InitiatorLeaveARequest_Exception() {
        ResponseEventDTO responseEventDTO = makeResponseEventTest();
        responseEventDTO.setRequestModeration(false);
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(responseEventDTO);

        ValidationException exception = assertThrowsExactly(ValidationException.class,
                () -> requestService.createRequest("1", 1L)
        );

        assertEquals("Event initiator cannot leave a request to participate in his event"
                , exception.getMessage());
    }


    @Test
    public void createRequestTest_UnpublishedEvent_Exception() {
        ResponseEventDTO responseEventDTO = makeResponseEventTest();
        responseEventDTO.setRequestModeration(false);
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(responseEventDTO);

        ValidationException exception = assertThrowsExactly(ValidationException.class,
                () -> requestService.createRequest("2", 1L)
        );

        assertEquals("User cannot participate in an unpublished event", exception.getMessage());

    }


    @Test
    public void createRequestTest_LimitOfRequests_Exception() {
        ResponseEventDTO responseEventDTO = makeResponseEventTest();
        responseEventDTO.setRequestModeration(false);
        responseEventDTO.setState(EventState.PUBLISHED);
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(responseEventDTO);

        ValidationException exception = assertThrowsExactly(ValidationException.class,
                () -> requestService.createRequest("2", 1L)
        );

        assertEquals("The event has reached the limit of requests for participation", exception.getMessage());

    }

    @Test
    public void cancelRequestTest_Success() {
        Request requestRequest = makeRequest();
        when(requestRepository.findRequestByRequesterIdAndEventId(any(), anyLong()))
                .thenReturn(requestRequest);
        ResponseParticipationRequestDTO responseRequest = requestService.cancelRequest("1", 1L);

        assertThat(responseRequest.getRequesterId(), equalTo(requestRequest.getRequesterId()));
        assertThat(responseRequest.getId(), equalTo(requestRequest.getId()));
        assertThat(responseRequest.getCreationDate(), equalTo(requestRequest.getCreationDate()));
        assertThat(responseRequest.getEventId(), equalTo(requestRequest.getEventId()));
        assertThat(responseRequest.getState(), equalTo(requestRequest.getState()));

        verify(requestRepository, times(1))
                .findRequestByRequesterIdAndEventId(any(), anyLong());
        verify(requestRepository, times(1)).delete(any());
    }


    @Test
    public void cancelRequestTest_RequestNotFoundException() {
        when(requestRepository.findRequestByRequesterIdAndEventId(any(), anyLong()))
                .thenReturn(null);

        RequestNotFountException exception = assertThrowsExactly(RequestNotFountException.class,
                () -> requestService.cancelRequest("1", 1L)
        );

        assertEquals("User with id 1 didn't leave a request to participate in event with id 1"
                , exception.getMessage());
    }

    @Test
    void setRequestsStatusTest_Success() {
        String userId = "1";
        Long eventId = 1L;
        ResponseEventDTO eventDTO = makeResponseEventTest();
        RequestParticipationRequestDTO requestDTO = makeRequestRequest();
        eventDTO.setParticipantLimit(4);
        eventDTO.setConfirmedRequests(0);
        when(eventServiceClient.findEventById(eventId))
                .thenReturn(eventDTO);
        when(requestRepository.getAcceptedEventsCount(eventId))
                .thenReturn(2);

        Request request1 = makeRequest();
        Request request2 = makeRequest();
        Request request3 = makeRequest();

        List<Request> requests = List.of(request1, request2, request3);
        when(requestRepository.findAllById(requestDTO.getRequestIds())).thenReturn(requests);

        List<ResponseParticipationRequestDTO> updatedRequests = requestService.setRequestsStatus(userId, eventId, requestDTO);

        assertEquals(3, updatedRequests.size());
        assertEquals(ParticipationRequestState.CONFIRMED, updatedRequests.get(0).getState());
        assertEquals(ParticipationRequestState.CONFIRMED, updatedRequests.get(1).getState());
        assertEquals(ParticipationRequestState.REJECTED, updatedRequests.get(2).getState());

        verify(requestRepository, times(1)).saveAllAndFlush(any());
        verify(confirmedRequestService, times(1)).sendConfirmedRequestMsg(any());
    }

    @Test
    void SetRequestsStatusTest_UserNotOwner_Exception() {
        String userId = "2";
        Long eventId = 1L;

        ResponseEventDTO eventDTO = makeResponseEventTest();
        RequestParticipationRequestDTO requestDTO = makeRequestRequest();
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(eventDTO);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> requestService.setRequestsStatus(userId, eventId, requestDTO));

        assertEquals("User with id 2 doesn't own event with id 1", exception.getMessage());
    }

    @Test
    void SetRequestsStatusTest_LimitReached_Exception() {
        String userId = "1";
        Long eventId = 1L;
        ResponseEventDTO eventDTO = makeResponseEventTest();
        RequestParticipationRequestDTO requestDTO = makeRequestRequest();
        eventDTO.setParticipantLimit(3);

        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(eventDTO);
        when(requestRepository.getAcceptedEventsCount(eventId)).thenReturn(3);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> requestService.setRequestsStatus(userId, eventId, requestDTO));

        assertEquals("Request limit to event with id 1 has been reached", exception.getMessage());
    }

}
