package controller;

import com.faspix.RequestApplication;
import com.faspix.client.EventServiceClient;
import com.faspix.controller.RequestController;
import com.faspix.dto.RequestParticipationRequestDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseParticipationRequestDTO;
import com.faspix.entity.Request;
import com.faspix.enums.EventState;
import com.faspix.enums.ParticipationRequestState;
import com.faspix.exception.RequestNotFountException;
import com.faspix.exception.ValidationException;
import com.faspix.repository.RequestRepository;
import com.faspix.service.RequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.EventFactory.makeResponseEventTest;
import static utility.RequestFactory.*;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {RequestApplication.class})
@AutoConfigureMockMvc
@Transactional
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestController requestController;

    @MockitoBean
    private EventServiceClient eventServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        requestRepository.deleteAll();
    }

    @Test
    public void createRequestTest_Success() throws Exception {
        Request request = makeRequest();
        request.setId(null);
        ResponseEventDTO eventDTO = makeResponseEventTest();
        eventDTO.setParticipantLimit(20);
        eventDTO.setState(EventState.PUBLISHED);
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(eventDTO);
        requestRepository.save(request);


        MvcResult mvcResult = mockMvc.perform(post("/requests/1")
                        .header("X-User-Id", 2)
//                        .pathInfo("/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseParticipationRequestDTO response = objectMapper.readValue(body, ResponseParticipationRequestDTO.class);

//        ResponseParticipationRequestDTO response = requestController.createRequest(2L, 1L);
        Request savedRequest = requestRepository.findRequestByRequesterIdAndEventId(2L, 1L);
        assertThat(savedRequest.getState(), equalTo(response.getState()));
    }

    @Test
    public void createRequestTest_RequestAlreadyExists_Exception() {
        ResponseEventDTO eventDTO = makeResponseEventTest();
        eventDTO.setState(EventState.PUBLISHED);
        eventDTO.setParticipantLimit(20);
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(eventDTO);
        requestController.createRequest(2L, 1L);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> requestController.createRequest(2L, 1L));
        assertEquals("User with id 2 already leave a request to participate in event with id 1", exception.getMessage());
    }

    @Test
    public void cancelRequestTest_Success() throws Exception {
        Request request = makeRequest();
        request.setId(null);
        requestRepository.save(request);



        MvcResult mvcResult = mockMvc.perform(post("/requests/1/cancel")
                                .header("X-User-Id", request.getRequesterId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseParticipationRequestDTO response = objectMapper.readValue(body, ResponseParticipationRequestDTO.class);



//        ResponseParticipationRequestDTO response = requestController
//                .cancelRequest(request.getRequesterId(), 1L);
        assertThat(response.getState(), equalTo(ParticipationRequestState.PENDING));
    }

    @Test
    public void cancelRequestTest_RequestNotFound_Exception() {
        RequestNotFountException exception = assertThrows(RequestNotFountException.class,
                () -> requestController.cancelRequest(1L, 1L));
        assertEquals("User with id 1 didn't leave a request to participate in event with id 1", exception.getMessage());
    }

    @Test
    public void getRequestsToMyEventTest() {
        Request request = makeRequest();
        request.setId(null);
        List<Request> requests = List.of(request);
        ResponseEventDTO eventDTO = makeResponseEventTest();
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(eventDTO);
        requestRepository.saveAll(requests);

        List<ResponseParticipationRequestDTO> response = requestController
                .getRequestsToMyEvent(request.getRequesterId(), 1L, 0, 10);
        assertThat(response.size(), equalTo(1));
    }

    @Test
    public void setRequestsStatusTest_Success() {
        Request request = makeRequest();
        request.setId(null);
        ResponseEventDTO eventDTO = makeResponseEventTest();
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(eventDTO);
        Request save = requestRepository.save(request);
        RequestParticipationRequestDTO requestDTO = makeRequestRequest();
        requestDTO.setRequestIds(List.of(save.getId()));

        List<ResponseParticipationRequestDTO> response = requestController
                .setRequestsStatus(request.getRequesterId(), 1L, requestDTO);
        assertThat(response.getFirst().getState(), equalTo(ParticipationRequestState.CONFIRMED));
    }

    @Test
    public void getUsersRequestsTest() {
        Request request = makeRequest();
        request.setId(null);
        requestRepository.save(request);

        List<ResponseParticipationRequestDTO> response = requestController
                .getUsersRequests(request.getRequesterId(), 0, 10);
        assertThat(response.size(), equalTo(1));
    }


}
