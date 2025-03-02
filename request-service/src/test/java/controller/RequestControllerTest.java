package controller;

import com.faspix.RequestApplication;
import com.faspix.client.EventServiceClient;
import com.faspix.controller.RequestController;
import com.faspix.dto.RequestParticipationRequestDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseParticipationRequestDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.entity.Request;
import com.faspix.enums.EventState;
import com.faspix.enums.ParticipationRequestState;
import com.faspix.repository.RequestRepository;
import com.faspix.service.ConfirmedRequestService;
import com.faspix.service.RequestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import confg.TestSecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.EventFactory.makeResponseEventTest;
import static utility.RequestFactory.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(classes = {RequestApplication.class})
@AutoConfigureMockMvc
@Testcontainers
@Import(TestSecurityConfiguration.class)
@WithMockUser(roles = {"USER", "ADMIN"})
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
    private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    @MockitoBean
    private ConfirmedRequestService confirmedRequestService;

    @MockitoBean
    private EventServiceClient eventServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final Jwt jwtTest = Jwt.withTokenValue("000")
            .header("1", 1)
            .claim("1", 1)
            .subject("1")
            .build();

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void init() {
        requestRepository.deleteAll();
    }

    @Test
    public void createRequestTest_Success() throws Exception {
        Long eventId = 1L;
        Request request = makeRequest();
        request.setRequesterId("2");
        ResponseEventDTO eventDTO = makeResponseEventTest();
        eventDTO.setParticipantLimit(20);
        eventDTO.setState(EventState.PUBLISHED);
        eventDTO.setInitiator(ResponseUserShortDTO.builder().userId("2").username("11").build());
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(eventDTO);
        requestRepository.save(request);

        MvcResult mvcResult = mockMvc.perform(post("/requests/events/{eventId}", eventId)
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseParticipationRequestDTO response = objectMapper.readValue(body, ResponseParticipationRequestDTO.class);

        Request savedRequest = requestRepository.findRequestByRequesterIdAndEventId("1", 1L);
        assertThat(savedRequest.getState(), equalTo(response.getState()));
        assertThat(savedRequest.getRequesterId(), equalTo(response.getRequesterId()));
        assertThat(savedRequest.getState(), equalTo(response.getState()));
        assertThat(savedRequest.getId(), equalTo(response.getId()));
    }

    @Test
    public void createRequestTest_RequestAlreadyExists_Exception() throws Exception {
        Long eventId = 1L;
        ResponseEventDTO eventDTO = makeResponseEventTest();
        eventDTO.setState(EventState.PUBLISHED);
        eventDTO.setParticipantLimit(20);
        eventDTO.setInitiator(ResponseUserShortDTO.builder().userId("2").username("11").build());
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(eventDTO);
        requestController.createRequest(jwtTest, 1L);

        mockMvc.perform(post("/requests/events/{eventId}", eventId)
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest());
    }

    @Test
    public void cancelRequestTest_Success() throws Exception {
        Long eventId = 1L;
        Request request = makeRequest();
        requestRepository.save(request);

        MvcResult mvcResult = mockMvc.perform(patch("/requests/events/{eventId}/cancel", eventId)
                                .header("Authorization", "Bearer 123123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseParticipationRequestDTO response = objectMapper.readValue(body, ResponseParticipationRequestDTO.class);

        assertThat(response.getState(), equalTo(ParticipationRequestState.PENDING));
    }

    @Test
    public void cancelRequestTest_RequestNotFound_Exception() throws Exception {
        Long eventId = 1L;
        mockMvc.perform(patch("/requests/events/{eventId}/cancel", eventId)
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound());
    }

    @Test
    public void getRequestsToMyEventTest_Success() throws Exception {
        Long eventId = 1L;
        Request request = makeRequest();
        List<Request> requests = List.of(request);
        ResponseEventDTO eventDTO = makeResponseEventTest();
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(eventDTO);
        requestRepository.saveAll(requests);

        MvcResult mvcResult = mockMvc.perform(get("/requests/events/{eventId}", eventId)
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseParticipationRequestDTO> response = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(response.size(), equalTo(1));
    }

    @Test
    public void setRequestsStatusTest_Success() throws Exception {
        Long eventId = 1L;
        Request request = makeRequest();
        ResponseEventDTO eventDTO = makeResponseEventTest();
        when(eventServiceClient.findEventById(anyLong()))
                .thenReturn(eventDTO);
        Request save = requestRepository.save(request);
        RequestParticipationRequestDTO requestDTO = makeRequestRequest();
        requestDTO.setRequestIds(List.of(save.getId()));

        MvcResult mvcResult = mockMvc.perform(patch("/requests/events/{eventId}", eventId)
                        .header("Authorization", "Bearer 123123")
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseParticipationRequestDTO> response = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(response.getFirst().getState(), equalTo(ParticipationRequestState.CONFIRMED));
    }

    @Test
    public void getUsersRequestsTest_Success() throws Exception {
        Request request = makeRequest();
        requestRepository.save(request);

        MvcResult mvcResult = mockMvc.perform(get("/requests/users")
                        .header("Authorization", "Bearer 123123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseParticipationRequestDTO> response = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(response.size(), equalTo(1));
    }


}
