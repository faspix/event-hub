package controller;

import com.faspix.EventApplication;
import com.faspix.client.CategoryServiceClient;
import com.faspix.client.UserServiceClient;
import com.faspix.controller.EventController;
import com.faspix.dto.*;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.enums.EventStateAction;
import com.faspix.repository.EventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.EventFactory.*;
import static utility.UserFactory.*;

@SpringBootTest(classes = {EventApplication.class})
@AutoConfigureMockMvc
@Transactional
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventController eventController;

    @MockitoBean
    private UserServiceClient userServiceClient;

    @MockitoBean
    private CategoryServiceClient categoryServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        eventRepository.deleteAll();
    }

    @Test
    public void createEventTest_Success() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        when(userServiceClient.getUserById(anyLong()))
                .thenReturn(makeResponseUserTest());

        MvcResult mvcResult = mockMvc.perform(post("/events")
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpectAll(jsonPath("$.requestModeration", is(requestEventDTO.getRequestModeration())))
                .andExpectAll(jsonPath("$.title", is(requestEventDTO.getTitle())))
                .andExpectAll(jsonPath("$.annotation", is(requestEventDTO.getAnnotation())))
                .andExpectAll(jsonPath("$.description", is(requestEventDTO.getDescription())))
                .andExpectAll(jsonPath("$.paid", is(requestEventDTO.getPaid())))
                .andExpectAll(jsonPath("$.participantLimit", is(requestEventDTO.getParticipantLimit())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO event = objectMapper.readValue(body, ResponseEventDTO.class);

        Event event1 = eventRepository.findById(event.getEventId()).get();

        assertThat(event.getEventId(), equalTo(event1.getEventId()));
        assertThat(event.getEventDate(), equalTo(event1.getEventDate()));
        assertThat(event.getAnnotation(), equalTo(event1.getAnnotation()));
        assertThat(event.getRequestModeration(), equalTo(event1.getRequestModeration()));
        assertThat(event.getPaid(), equalTo(event1.getPaid()));
        assertThat(event.getDescription(), equalTo(event1.getDescription()));
        assertThat(event.getLocation(), equalTo(event1.getLocation()));
        assertThat(event.getParticipantLimit(), equalTo(event1.getParticipantLimit()));
        assertThat(event.getTitle(), equalTo(event1.getTitle()));
        assertThat(event.getState(), equalTo(EventState.PENDING));
    }


    @Test
    public void createEventTest_EventStartsToSoon_Success() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2).plusMinutes(1));
        when(userServiceClient.getUserById(anyLong()))
                .thenReturn(makeResponseUserTest());


        MvcResult mvcResult = mockMvc.perform(post("/events")
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpectAll(jsonPath("$.requestModeration", is(requestEventDTO.getRequestModeration())))
                .andExpectAll(jsonPath("$.title", is(requestEventDTO.getTitle())))
                .andExpectAll(jsonPath("$.annotation", is(requestEventDTO.getAnnotation())))
                .andExpectAll(jsonPath("$.description", is(requestEventDTO.getDescription())))
                .andExpectAll(jsonPath("$.paid", is(requestEventDTO.getPaid())))
                .andExpectAll(jsonPath("$.participantLimit", is(requestEventDTO.getParticipantLimit())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO response = objectMapper.readValue(body, ResponseEventDTO.class);

        Event event1 = eventRepository.findById(response.getEventId()).get();

        assertThat(response.getEventId(), equalTo(event1.getEventId()));
        assertThat(response.getEventDate(), equalTo(event1.getEventDate()));
        assertThat(response.getAnnotation(), equalTo(event1.getAnnotation()));
        assertThat(response.getRequestModeration(), equalTo(event1.getRequestModeration()));
        assertThat(response.getPaid(), equalTo(event1.getPaid()));
        assertThat(response.getDescription(), equalTo(event1.getDescription()));
        assertThat(response.getLocation(), equalTo(event1.getLocation()));
        assertThat(response.getParticipantLimit(), equalTo(event1.getParticipantLimit()));
        assertThat(response.getTitle(), equalTo(event1.getTitle()));
        assertThat(response.getState(), equalTo(EventState.PENDING));
    }

    @Test
    public void createEventTest_EventStartsToSoon_Exception() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2));

        mockMvc.perform(post("/events")
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest());

    }

    @Test
    public void editEventTest_Success() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setTitle("updated title");
        Event savedEvent = eventRepository.save(makeEventTest());

        MvcResult mvcResult = mockMvc.perform(patch("/events/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andExpectAll(jsonPath("$.requestModeration", is(requestEventDTO.getRequestModeration())))
                .andExpectAll(jsonPath("$.title", is(requestEventDTO.getTitle())))
                .andExpectAll(jsonPath("$.annotation", is(requestEventDTO.getAnnotation())))
                .andExpectAll(jsonPath("$.description", is(requestEventDTO.getDescription())))
                .andExpectAll(jsonPath("$.paid", is(requestEventDTO.getPaid())))
                .andExpectAll(jsonPath("$.participantLimit", is(requestEventDTO.getParticipantLimit())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO response = objectMapper.readValue(body, ResponseEventDTO.class);

        Event event1 = eventRepository.findById(response.getEventId()).get();

        assertThat(response.getEventId(), equalTo(event1.getEventId()));
        assertThat(response.getEventDate(), equalTo(event1.getEventDate()));
        assertThat(response.getAnnotation(), equalTo(event1.getAnnotation()));
        assertThat(response.getRequestModeration(), equalTo(event1.getRequestModeration()));
        assertThat(response.getPaid(), equalTo(event1.getPaid()));
        assertThat(response.getDescription(), equalTo(event1.getDescription()));
        assertThat(response.getLocation(), equalTo(event1.getLocation()));
        assertThat(response.getParticipantLimit(), equalTo(event1.getParticipantLimit()));
        assertThat(response.getTitle(), equalTo(event1.getTitle()));
        assertThat(response.getState(), equalTo(EventState.PENDING));
    }


    @Test
    public void editEventTest_EventStartsToSoon_Success() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setTitle("updated title");
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2).plusMinutes(1));
        Event savedEvent = eventRepository.save(makeEventTest());

        MvcResult mvcResult = mockMvc.perform(patch("/events/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andExpectAll(jsonPath("$.requestModeration", is(requestEventDTO.getRequestModeration())))
                .andExpectAll(jsonPath("$.title", is(requestEventDTO.getTitle())))
                .andExpectAll(jsonPath("$.annotation", is(requestEventDTO.getAnnotation())))
                .andExpectAll(jsonPath("$.description", is(requestEventDTO.getDescription())))
                .andExpectAll(jsonPath("$.paid", is(requestEventDTO.getPaid())))
                .andExpectAll(jsonPath("$.participantLimit", is(requestEventDTO.getParticipantLimit())))
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO response = objectMapper.readValue(body, ResponseEventDTO.class);

        Event event1 = eventRepository.findById(response.getEventId()).get();

        assertThat(response.getEventId(), equalTo(event1.getEventId()));
        assertThat(response.getEventDate(), equalTo(event1.getEventDate()));
        assertThat(response.getAnnotation(), equalTo(event1.getAnnotation()));
        assertThat(response.getRequestModeration(), equalTo(event1.getRequestModeration()));
        assertThat(response.getPaid(), equalTo(event1.getPaid()));
        assertThat(response.getDescription(), equalTo(event1.getDescription()));
        assertThat(response.getLocation(), equalTo(event1.getLocation()));
        assertThat(response.getParticipantLimit(), equalTo(event1.getParticipantLimit()));
        assertThat(response.getTitle(), equalTo(event1.getTitle()));
        assertThat(response.getState(), equalTo(EventState.PENDING));

    }

    @Test
    public void editEventTest_EventStartsToSoon_Exception() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setTitle("updated title");
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2));
        Event savedEvent = eventRepository.save(makeEventTest());

        mockMvc.perform(patch("/events/{eventId}", savedEvent.getEventId())
                .content(objectMapper.writeValueAsString(requestEventDTO))
                .header("X-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }


    @Test
    public void editEventTest_UserDidntCreateEvent_Exception() throws Exception {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        Event event = makeEventTest();
        event.setInitiatorId(22L);

        Event savedEvent = eventRepository.save(event);

        mockMvc.perform(patch("/events/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(requestEventDTO))
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest());

    }

    @Test
    public void editEventTest_PublishedEvent_Exception() throws Exception {
        Event event = makeEventTest();
        event.setState(EventState.PUBLISHED);
        RequestEventDTO requestEventDTO = makeRequestEventTest();

        Event savedEvent = eventRepository.save(event);

        mockMvc.perform(patch("/events/{eventId}", savedEvent.getEventId())
                .content(objectMapper.writeValueAsString(requestEventDTO))
                .header("X-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }

    @Test
    public void findEventByIdTest_Success() throws Exception {
        Event savedEvent = eventRepository.save(makeEventTest());


        MvcResult mvcResult = mockMvc.perform(get("/events/{eventId}", savedEvent.getEventId())
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO result = objectMapper.readValue(body, ResponseEventDTO.class);

        assertThat(result.getEventId(), equalTo(savedEvent.getEventId()));
        assertThat(result.getTitle(), equalTo(savedEvent.getTitle()));
        assertThat(result.getAnnotation(), equalTo(savedEvent.getAnnotation()));
        assertThat(result.getDescription(), equalTo(savedEvent.getDescription()));
        assertThat(result.getEventDate(), equalTo(savedEvent.getEventDate()));
        assertThat(result.getLocation(), equalTo(savedEvent.getLocation()));
        assertThat(result.getPaid(), equalTo(savedEvent.getPaid()));
        assertThat(result.getState(), equalTo(savedEvent.getState()));
        assertThat(result.getViews(), equalTo(savedEvent.getViews()));
    }

    @Test
    public void findEventByIdTest_EventNotFound_ThrowsException() throws Exception {
        Long eventId = 100L;
        mockMvc.perform(get("/events/{eventId}", eventId)
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound());
    }

    @Test
    public void findEventsByCategoryIdTest_Success() throws Exception {
        Event savedEvent = eventRepository.save(makeEventTest());

        MvcResult mvcResult = mockMvc.perform(get("/events/categories/{categoryId}",
                                                                        savedEvent.getCategoryId())
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseEventShortDTO> resultList = objectMapper.readValue(body, new TypeReference<>() {});
        System.out.println(resultList);
        ResponseEventShortDTO result = resultList.getFirst();

        assertThat(result.getEventId(), equalTo(savedEvent.getEventId()));
        assertThat(result.getTitle(), equalTo(savedEvent.getTitle()));
        assertThat(result.getAnnotation(), equalTo(savedEvent.getAnnotation()));
        assertThat(result.getEventDate(), equalTo(savedEvent.getEventDate()));
        assertThat(result.getPaid(), equalTo(savedEvent.getPaid()));
        assertThat(result.getViews(), equalTo(savedEvent.getViews()));
    }

    @Test
    public void findAllUserEventsTest_Success() throws Exception {
        Long eventId = 1L;
        Event event1 = makeEventTest();
        event1.setTitle("Title 1");
        event1.setEventId(null);
        eventRepository.save(event1);

        Event event2 = makeEventTest();
        event2.setTitle("Title 2");
        event2.setEventId(null);
        eventRepository.save(event2);

        MvcResult mvcResult = mockMvc.perform(get("/events/users/{eventId}", eventId)
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseEventShortDTO> events = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(events.size(), equalTo(2));
        assertThat(events.get(0).getTitle(), equalTo(event1.getTitle()));
        assertThat(events.get(1).getTitle(), equalTo(event2.getTitle()));
    }

    @Test
    public void findAllUserEventsTest_UserHasNoEvents_ReturnsEmptyList() throws Exception {
        Long eventId = 100L;
        MvcResult mvcResult = mockMvc.perform(get("/events/users/{eventId}", eventId)
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<ResponseEventShortDTO> events = objectMapper.readValue(body, new TypeReference<>() {});

        assertThat(events.size(), equalTo(0));
    }

    @Test
    public void setConfirmedRequestsNumberTest() {
        Event savedEvent = eventRepository.save(makeEventTest());

        ConfirmedRequestsDTO requestDTO = new ConfirmedRequestsDTO(savedEvent.getEventId(), 5);
        ResponseEntity<HttpStatus> response = eventController.setConfirmedRequestsNumber(requestDTO);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void adminEditEventTest_Success() throws Exception {
        Event savedEvent = eventRepository.save(makeEventTest());

        RequestUpdateEventAdminDTO adminDTO = makeAdminRequest();

        MvcResult mvcResult = mockMvc.perform(patch("/events/admin/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(adminDTO))
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO response = objectMapper.readValue(body, ResponseEventDTO.class);

        Event result = eventRepository.findById(savedEvent.getEventId()).get();

        assertThat(result.getEventDate(), equalTo(adminDTO.getEventDate()));
        assertThat(result.getTitle(), equalTo(adminDTO.getTitle()));
        assertThat(result.getAnnotation(), equalTo(adminDTO.getAnnotation()));
        assertThat(result.getDescription(), equalTo(adminDTO.getDescription()));
        assertThat(result.getLocation(), equalTo(adminDTO.getLocation()));
        assertThat(result.getPaid(), equalTo(adminDTO.getPaid()));

        assertThat(response.getEventDate(), equalTo(adminDTO.getEventDate()));
        assertThat(response.getTitle(), equalTo(adminDTO.getTitle()));
        assertThat(response.getAnnotation(), equalTo(adminDTO.getAnnotation()));
        assertThat(response.getDescription(), equalTo(adminDTO.getDescription()));
        assertThat(response.getLocation(), equalTo(adminDTO.getLocation()));
        assertThat(result.getPaid(), equalTo(adminDTO.getPaid()));
    }

    @Test
    public void adminEditEventTest_EventStartsToSoon_Exception() throws Exception {
        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();
        Event event = makeEventTest();
        event.setEventDate(LocalDateTime.now().plusHours(1));
        Event savedEvent = eventRepository.save(event);

       mockMvc.perform(patch("/events/admin/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(adminRequest))
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest());
    }

    @Test
    public void adminEditEventTest_EventStartsToSoon_Success() throws Exception {
        Event event = makeEventTest();
        event.setEventDate(LocalDateTime.now().plusHours(2));
        Event savedEvent = eventRepository.save(event);

        RequestUpdateEventAdminDTO adminDTO = makeAdminRequest();

        MvcResult mvcResult = mockMvc.perform(patch("/events/admin/{eventId}", savedEvent.getEventId())
                        .content(objectMapper.writeValueAsString(adminDTO))
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO response = objectMapper.readValue(body, ResponseEventDTO.class);

        Event result = eventRepository.findById(savedEvent.getEventId()).get();

        assertThat(result.getEventDate(), equalTo(adminDTO.getEventDate()));
        assertThat(result.getTitle(), equalTo(adminDTO.getTitle()));
        assertThat(result.getAnnotation(), equalTo(adminDTO.getAnnotation()));
        assertThat(result.getDescription(), equalTo(adminDTO.getDescription()));
        assertThat(result.getLocation(), equalTo(adminDTO.getLocation()));
        assertThat(result.getPaid(), equalTo(adminDTO.getPaid()));

        assertThat(response.getEventDate(), equalTo(adminDTO.getEventDate()));
        assertThat(response.getTitle(), equalTo(adminDTO.getTitle()));
        assertThat(response.getAnnotation(), equalTo(adminDTO.getAnnotation()));
        assertThat(response.getDescription(), equalTo(adminDTO.getDescription()));
        assertThat(response.getLocation(), equalTo(adminDTO.getLocation()));
        assertThat(result.getPaid(), equalTo(adminDTO.getPaid()));

    }

    @Test
    public void adminEditEventTest_InvalidState_Exception() throws Exception {
        Event event = makeEventTest();
        event.setState(EventState.CANCELED);
        Event savedEvent = eventRepository.save(event);

        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();
        adminRequest.setStateAction(EventStateAction.PUBLISH_EVENT);

        mockMvc.perform(patch("/events/admin/{eventId}", savedEvent.getEventId())
                .content(objectMapper.writeValueAsString(adminRequest))
                .header("X-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }


}
