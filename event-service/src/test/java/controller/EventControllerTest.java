package controller;

import com.faspix.EventApplication;
import com.faspix.client.CategoryServiceClient;
import com.faspix.client.UserServiceClient;
import com.faspix.controller.EventController;
import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.RequestUpdateEventAdminDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.exception.ValidationException;
import com.faspix.repository.EventRepository;
import org.hibernate.annotations.Fetch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    public void createEventTest() {
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
    public void createEventTest_ValidationException_2hoursBoundaryValues_NotThrowsException() {
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
    public void createEventTest_ValidationException_2hoursException_ThrowsException() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2));

        Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventController.createEvent(1L, requestEventDTO)
        );
    }

    @Test
    public void editEventTest() {
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
    public void editEventTest_ValidationException_2hoursBoundaryValues_NotThrowsException() {
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
    public void editEventTest_ValidationException_2hoursException_ThrowsException() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setTitle("updated title");
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2));
        Event event = makeEventTest();
        event.setEventId(null);
        Event savedEvent = eventRepository.save(event);

        Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventController.editEvent(1L, savedEvent.getEventId(), requestEventDTO)
        );
    }


    @Test
    public void editEventTest_ValidationException_UserDidntCreateEvent_ThrowsException() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        Event event = makeEventTest();
        event.setEventId(null);
        event.setInitiatorId(22L);

        Event savedEvent = eventRepository.save(event);

        Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventController.editEvent(1L, savedEvent.getEventId(), requestEventDTO)
        );
    }

    @Test
    public void editEventTest_ValidationException_PublishedEvent_ThrowsException() {
        Event event = makeEventTest();
        event.setState(EventState.PUBLISHED);
        event.setEventId(null);
        RequestEventDTO requestEventDTO = makeRequestEventTest();

        Event savedEvent = eventRepository.save(event);

        Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventController.editEvent(1L, savedEvent.getEventId(), requestEventDTO)
        );
    }

    @Test
    public void findEventByIdTest() {
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
    public void findEventsByCategoryIdTest() {
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
    public void adminEditEventTest() {
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
    public void adminEditEventTest_ValidationException_1hoursException_ThrowsException() {
        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();
        Event event = makeEventTest();
        event.setEventId(null);
        event.setEventDate(LocalDateTime.now().plusHours(1));
        Event savedEvent = eventRepository.save(event);

        Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventController.adminEditEvent(savedEvent.getEventId(), adminRequest)
        );
    }

    @Test
    public void adminEditEventTest_ValidationException_1hoursBoundaryValues_NotThrowsException() {
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

}
