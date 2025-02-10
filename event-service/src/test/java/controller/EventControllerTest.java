package controller;

import com.faspix.EventApplication;
import com.faspix.client.CategoryServiceClient;
import com.faspix.client.UserServiceClient;
import com.faspix.controller.EventController;
import com.faspix.dto.*;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.enums.EventStateAction;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.ValidationException;
import com.faspix.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
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

    @BeforeEach
    void init() {
        eventRepository.deleteAll();
    }

    @Test
    public void createEventTest_Success() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        when(userServiceClient.getUserById(anyLong()))
                .thenReturn(makeResponseUserTest());

        ResponseEventDTO event = eventController.createEvent(1L, requestEventDTO);

        assertThat(event.getEventDate(), equalTo(requestEventDTO.getEventDate()));
        assertThat(event.getAnnotation(), equalTo(requestEventDTO.getAnnotation()));
        assertThat(event.getRequestModeration(), equalTo(requestEventDTO.getRequestModeration()));
        assertThat(event.getPaid(), equalTo(requestEventDTO.getPaid()));
        assertThat(event.getDescription(), equalTo(requestEventDTO.getDescription()));
        assertThat(event.getLocation(), equalTo(requestEventDTO.getLocation()));
        assertThat(event.getParticipantLimit(), equalTo(requestEventDTO.getParticipantLimit()));
        assertThat(event.getTitle(), equalTo(requestEventDTO.getTitle()));
        assertThat(event.getState(), equalTo(EventState.PENDING));

    }


    @Test
    public void createEventTest_EventStartsToSoon_Success() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2).plusMinutes(1));
        when(userServiceClient.getUserById(anyLong()))
                .thenReturn(makeResponseUserTest());

        ResponseEventDTO response = eventController.createEvent(1L, requestEventDTO);

        assertThat(response.getAnnotation(), equalTo(requestEventDTO.getAnnotation()));
        assertThat(response.getRequestModeration(), equalTo(requestEventDTO.getRequestModeration()));
        assertThat(response.getPaid(), equalTo(requestEventDTO.getPaid()));
        assertThat(response.getDescription(), equalTo(requestEventDTO.getDescription()));
        assertThat(response.getLocation(), equalTo(requestEventDTO.getLocation()));
        assertThat(response.getParticipantLimit(), equalTo(requestEventDTO.getParticipantLimit()));
        assertThat(response.getTitle(), equalTo(requestEventDTO.getTitle()));
        assertThat(response.getState(), equalTo(EventState.PENDING));

    }

    @Test
    public void createEventTest_EventStartsToSoon_Exception() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2));

        ValidationException exception = assertThrowsExactly(ValidationException.class,
                () -> eventController.createEvent(1L, requestEventDTO)
        );
        assertEquals("Event cannot start in less than 2 hours", exception.getMessage());
    }

    @Test
    public void editEventTest_Success() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setTitle("updated title");
        Event event = makeEventTest();
        event.setEventId(null);
        Event savedEvent = eventRepository.save(event);

        ResponseEventDTO response = eventController.editEvent(1L,
                savedEvent.getEventId(), requestEventDTO);

        assertThat(response.getEventDate(), equalTo(requestEventDTO.getEventDate()));
        assertThat(response.getRequestModeration(), equalTo(requestEventDTO.getRequestModeration()));
        assertThat(response.getTitle(), equalTo(requestEventDTO.getTitle()));
        assertThat(response.getAnnotation(), equalTo(requestEventDTO.getAnnotation()));
        assertThat(response.getDescription(), equalTo(requestEventDTO.getDescription()));
        assertThat(response.getLocation(), equalTo(requestEventDTO.getLocation()));
        assertThat(response.getPaid(), equalTo(requestEventDTO.getPaid()));

    }


    @Test
    public void editEventTest_EventStartsToSoon_Success() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setTitle("updated title");
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2).plusMinutes(1));
        Event event = makeEventTest();
        event.setEventId(null);
        Event savedEvent = eventRepository.save(event);

        ResponseEventDTO response = eventController.editEvent(1L,
                savedEvent.getEventId(), requestEventDTO);

        assertThat(response.getEventDate(), equalTo(requestEventDTO.getEventDate()));
        assertThat(response.getRequestModeration(), equalTo(requestEventDTO.getRequestModeration()));
        assertThat(response.getTitle(), equalTo(requestEventDTO.getTitle()));
        assertThat(response.getAnnotation(), equalTo(requestEventDTO.getAnnotation()));
        assertThat(response.getDescription(), equalTo(requestEventDTO.getDescription()));
        assertThat(response.getLocation(), equalTo(requestEventDTO.getLocation()));
        assertThat(response.getPaid(), equalTo(requestEventDTO.getPaid()));
    }

    @Test
    public void editEventTest_EventStartsToSoon_Exception() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setTitle("updated title");
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2));
        Event event = makeEventTest();
        event.setEventId(null);
        Event savedEvent = eventRepository.save(event);

        ValidationException exception = assertThrowsExactly(ValidationException.class,
                () -> eventController.editEvent(1L, savedEvent.getEventId(), requestEventDTO)
        );
        assertEquals("Event cannot start in less than 2 hours", exception.getMessage());
    }


    @Test
    public void editEventTest_UserDidntCreateEvent_Exception() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        Event event = makeEventTest();
        event.setEventId(null);
        event.setInitiatorId(22L);

        Event savedEvent = eventRepository.save(event);

        ValidationException exception = assertThrowsExactly(ValidationException.class,
                () -> eventController.editEvent(1L, savedEvent.getEventId(), requestEventDTO)
        );
    }

    @Test
    public void editEventTest_PublishedEvent_Exception() {
        Event event = makeEventTest();
        event.setState(EventState.PUBLISHED);
        event.setEventId(null);
        RequestEventDTO requestEventDTO = makeRequestEventTest();

        Event savedEvent = eventRepository.save(event);

        ValidationException exception = assertThrowsExactly(ValidationException.class,
                () -> eventController.editEvent(1L, savedEvent.getEventId(), requestEventDTO)
        );
        assertEquals("Event must not be published", exception.getMessage());
    }

    @Test
    public void findEventByIdTest_Success() {
        Event event = makeEventTest();
        event.setEventId(null);

        Event savedEvent = eventRepository.save(event);
        ResponseEventDTO result = eventController.findEventById(savedEvent.getEventId());

        assertThat(result.getEventId(), equalTo(savedEvent.getEventId()));
        assertThat(result.getTitle(), equalTo(savedEvent.getTitle()));
        assertThat(result.getAnnotation(), equalTo(savedEvent.getAnnotation()));
        assertThat(result.getDescription(), equalTo(savedEvent.getDescription()));
        assertThat(result.getEventDate(), equalTo(savedEvent.getEventDate()));
        assertThat(result.getPublishedOn(), equalTo(savedEvent.getPublishedOn()));
        assertThat(result.getCreationDate(), equalTo(savedEvent.getCreationDate()));
        assertThat(result.getLocation(), equalTo(savedEvent.getLocation()));
        assertThat(result.getPaid(), equalTo(savedEvent.getPaid()));
        assertThat(result.getState(), equalTo(savedEvent.getState()));
        assertThat(result.getViews(), equalTo(savedEvent.getViews()));
    }

    @Test
    public void findEventByIdTest_EventNotFound_ThrowsException() {
        EventNotFoundException exception = assertThrowsExactly(EventNotFoundException.class,
                () -> eventController.findEventById(100L)
        );
        assertEquals("Event with id 100 not found", exception.getMessage());
    }

    @Test
    public void findEventsByCategoryIdTest_Success() {
        Event event = makeEventTest();
        event.setEventId(null);

        Event savedEvent = eventRepository.save(event);

        ResponseEventShortDTO result = eventController
                .findEventsByCategoryId(savedEvent.getCategoryId())
                .getFirst();

        assertThat(result.getEventId(), equalTo(savedEvent.getEventId()));
        assertThat(result.getTitle(), equalTo(savedEvent.getTitle()));
        assertThat(result.getAnnotation(), equalTo(savedEvent.getAnnotation()));
        assertThat(result.getEventDate(), equalTo(savedEvent.getEventDate()));
        assertThat(result.getPaid(), equalTo(savedEvent.getPaid()));
        assertThat(result.getViews(), equalTo(savedEvent.getViews()));
    }

    @Test
    public void findAllUserEventsTest_Success() {
        Event event1 = makeEventTest();
        event1.setTitle("Title 1");
        event1.setEventId(null);
        eventRepository.save(event1);

        Event event2 = makeEventTest();
        event2.setTitle("Title 2");
        event2.setEventId(null);
        eventRepository.save(event2);

        List<ResponseEventShortDTO> events = eventController.findAllUserEvents(1L, 0, 10);

        assertThat(events.size(), equalTo(2));
        assertThat(events.get(0).getTitle(), equalTo(event1.getTitle()));
        assertThat(events.get(1).getTitle(), equalTo(event2.getTitle()));
    }

    @Test
    public void findAllUserEventsTest_UserHasNoEvents_ReturnsEmptyList() {
        List<ResponseEventShortDTO> events = eventController.findAllUserEvents(100L, 0, 10);
        assertThat(events.size(), equalTo(0));
    }

    @Test
    public void setConfirmedRequestsNumberTest() {
        Event event = makeEventTest();
        event.setEventId(null);
        Event savedEvent = eventRepository.save(event);

        ConfirmedRequestsDTO requestDTO = new ConfirmedRequestsDTO(savedEvent.getEventId(), 5);
        ResponseEntity<HttpStatus> response = eventController.setConfirmedRequestsNumber(requestDTO);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void adminEditEventTest_Success() {
        Event event = makeEventTest();
        event.setEventId(null);
        Event savedEvent = eventRepository.save(event);

        RequestUpdateEventAdminDTO adminDTO = makeAdminRequest();
        eventController.adminEditEvent(savedEvent.getEventId(), adminDTO);

        Event result = eventRepository.findById(savedEvent.getEventId()).get();

        assertThat(result.getEventDate(), equalTo(adminDTO.getEventDate()));
        assertThat(result.getTitle(), equalTo(adminDTO.getTitle()));
        assertThat(result.getAnnotation(), equalTo(adminDTO.getAnnotation()));
        assertThat(result.getDescription(), equalTo(adminDTO.getDescription()));
        assertThat(result.getLocation(), equalTo(adminDTO.getLocation()));
        assertThat(result.getPaid(), equalTo(adminDTO.getPaid()));
    }

    @Test
    public void adminEditEventTest_EventStartsToSoon_Exception() {
        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();
        Event event = makeEventTest();
        event.setEventId(null);
        event.setEventDate(LocalDateTime.now().plusHours(1));
        Event savedEvent = eventRepository.save(event);

        ValidationException exception = assertThrowsExactly(ValidationException.class,
                () -> eventController.adminEditEvent(savedEvent.getEventId(), adminRequest)
        );
    }

    @Test
    public void adminEditEventTest_EventStartsToSoon_Success() {
        Event event = makeEventTest();
        event.setEventId(null);
        event.setEventDate(LocalDateTime.now().plusHours(2));
        Event savedEvent = eventRepository.save(event);

        RequestUpdateEventAdminDTO adminDTO = makeAdminRequest();
        eventController.adminEditEvent(savedEvent.getEventId(), adminDTO);

        Event result = eventRepository.findById(savedEvent.getEventId()).get();

        assertThat(result.getEventDate(), equalTo(adminDTO.getEventDate()));
        assertThat(result.getTitle(), equalTo(adminDTO.getTitle()));
        assertThat(result.getAnnotation(), equalTo(adminDTO.getAnnotation()));
        assertThat(result.getDescription(), equalTo(adminDTO.getDescription()));
        assertThat(result.getLocation(), equalTo(adminDTO.getLocation()));
        assertThat(result.getPaid(), equalTo(adminDTO.getPaid()));
    }

    @Test
    public void adminEditEventTest_InvalidState_Exception() {
        Event event = makeEventTest();
        event.setState(EventState.CANCELED);
        event.setEventId(null);
        Event savedEvent = eventRepository.save(event);

        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();
        adminRequest.setStateAction(EventStateAction.PUBLISH_EVENT);

        ValidationException exception = assertThrowsExactly(ValidationException.class,
                () -> eventController.adminEditEvent(savedEvent.getEventId(), adminRequest)
        );
        assertEquals("Event must be in PENDING state", exception.getMessage());
    }


}
