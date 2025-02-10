package service;

import com.faspix.client.UserServiceClient;
import com.faspix.dto.ConfirmedRequestsDTO;
import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.RequestUpdateEventAdminDTO;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.exception.ValidationException;
import com.faspix.mapper.EventMapper;
import com.faspix.repository.EventRepository;
import com.faspix.service.EventServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static utility.EventFactory.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private EventServiceImpl eventService;

    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);


    @Test
    public void createEventTest() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        when(eventRepository.save(any()))
                .thenReturn(makeEventTest());

        Event event = eventService.createEvent(1L, requestEventDTO);

        assertThat(event.getEventDate(), equalTo(requestEventDTO.getEventDate()));
        assertThat(event.getAnnotation(), equalTo(requestEventDTO.getAnnotation()));
        assertThat(event.getRequestModeration(), equalTo(requestEventDTO.getRequestModeration()));
        assertThat(event.getPaid(), equalTo(requestEventDTO.getPaid()));
        assertThat(event.getDescription(), equalTo(requestEventDTO.getDescription()));
        assertThat(event.getCategoryId(), equalTo(requestEventDTO.getCategoryId()));
        assertThat(event.getParticipantLimit(), equalTo(requestEventDTO.getParticipantLimit()));
        assertThat(event.getTitle(), equalTo(requestEventDTO.getTitle()));
        assertThat(event.getState(), equalTo(EventState.PENDING));

    }

    @Test
    public void createEventTest_ValidationException_2hoursBoundaryValues_NotThrowsException() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2).plusMinutes(1));
        when(eventRepository.save(any()))
                .thenReturn(makeEventTest());

        Event event = eventService.createEvent(1L, requestEventDTO);

        assertThat(event.getAnnotation(), equalTo(requestEventDTO.getAnnotation()));
        assertThat(event.getRequestModeration(), equalTo(requestEventDTO.getRequestModeration()));
        assertThat(event.getPaid(), equalTo(requestEventDTO.getPaid()));
        assertThat(event.getDescription(), equalTo(requestEventDTO.getDescription()));
        assertThat(event.getCategoryId(), equalTo(requestEventDTO.getCategoryId()));
        assertThat(event.getParticipantLimit(), equalTo(requestEventDTO.getParticipantLimit()));
        assertThat(event.getTitle(), equalTo(requestEventDTO.getTitle()));
        assertThat(event.getState(), equalTo(EventState.PENDING));

    }

    @Test
    public void createEventTest_ValidationException_2hoursException_ThrowsException() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(LocalDateTime.now());

        Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventService.createEvent(1L, requestEventDTO)
        );
    }

    @Test
    public void editEventTest() {
        Event event = makeEventTest();
        event.setTitle("updated event title");
        RequestEventDTO updateRequest = makeRequestEventTest();
        updateRequest.setTitle(event.getTitle());
        when(eventRepository.save(any()))
                .thenReturn(event);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(makeEventTest()));

        Event updatedEvent = eventService.editEvent(1L, 1L, updateRequest);

        assertThat(updatedEvent.getEventId(), equalTo(event.getEventId()));
        assertThat(updatedEvent.getEventDate(), equalTo(event.getEventDate()));
        assertThat(updatedEvent.getTitle(), equalTo(event.getTitle()));
        assertThat(updatedEvent.getAnnotation(), equalTo(event.getAnnotation()));
        assertThat(updatedEvent.getDescription(), equalTo(event.getDescription()));
        assertThat(updatedEvent.getLocation(), equalTo(event.getLocation()));
        assertThat(updatedEvent.getCreationDate(), equalTo(event.getCreationDate()));
        assertThat(updatedEvent.getPublishedOn(), equalTo(event.getPublishedOn()));
        assertThat(updatedEvent.getPaid(), equalTo(event.getPaid()));
        assertThat(updatedEvent.getState(), equalTo(event.getState()));
        assertThat(updatedEvent.getViews(), equalTo(event.getViews()));
        assertThat(updatedEvent.getInitiatorId(), equalTo(event.getInitiatorId()));

    }


    @Test
    public void editEventTest_ValidationException_2hoursException_ThrowsException() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        requestEventDTO.setEventDate(LocalDateTime.now().plusHours(2));

        Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventService.editEvent(1L, 1L, requestEventDTO)
        );
    }


    @Test
    public void editEventTest_ValidationException_2hoursBoundaryValues_NotThrowsException() {
        Event event = makeEventTest();
        event.setTitle("updated event title");
        event.setEventDate(LocalDateTime.now().plusHours(2).plusMinutes(1));
        RequestEventDTO updateRequest = makeRequestEventTest();
        updateRequest.setTitle(event.getTitle());
        updateRequest.setEventDate(event.getEventDate());
        when(eventRepository.save(any()))
                .thenReturn(event);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(makeEventTest()));

        Event updatedEvent = eventService.editEvent(1L, 1L, updateRequest);

        assertThat(updatedEvent.getEventId(), equalTo(event.getEventId()));
        assertThat(updatedEvent.getEventDate(), equalTo(event.getEventDate()));
        assertThat(updatedEvent.getTitle(), equalTo(event.getTitle()));
        assertThat(updatedEvent.getAnnotation(), equalTo(event.getAnnotation()));
        assertThat(updatedEvent.getDescription(), equalTo(event.getDescription()));
        assertThat(updatedEvent.getLocation(), equalTo(event.getLocation()));
        assertThat(updatedEvent.getCreationDate(), equalTo(event.getCreationDate()));
        assertThat(updatedEvent.getPublishedOn(), equalTo(event.getPublishedOn()));
        assertThat(updatedEvent.getPaid(), equalTo(event.getPaid()));
        assertThat(updatedEvent.getState(), equalTo(event.getState()));
        assertThat(updatedEvent.getViews(), equalTo(event.getViews()));
        assertThat(updatedEvent.getInitiatorId(), equalTo(event.getInitiatorId()));

    }

    @Test
    public void editEventTest_ValidationException_UserDidntCreateEvent_ThrowsException() {
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        Event event = makeEventTest();
        event.setInitiatorId(22L);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));

        Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventService.editEvent(1L, 1L, requestEventDTO)
        );
    }

    @Test
    public void editEventTest_ValidationException_PublishedEvent_ThrowsException() {
        Event event = makeEventTest();
        event.setState(EventState.PUBLISHED);
        RequestEventDTO requestEventDTO = makeRequestEventTest();
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));

        Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventService.editEvent(1L, 1L, requestEventDTO)
        );
    }

    @Test
    public void findEventTest() {
        Event event = makeEventTest();
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));

        Event result = eventService.findEventById(1L);

        assertThat(result.getEventId(), equalTo(event.getEventId()));
        assertThat(result.getEventDate(), equalTo(event.getEventDate()));
        assertThat(result.getTitle(), equalTo(event.getTitle()));
        assertThat(result.getAnnotation(), equalTo(event.getAnnotation()));
        assertThat(result.getDescription(), equalTo(event.getDescription()));
        assertThat(result.getLocation(), equalTo(event.getLocation()));
        assertThat(result.getCreationDate(), equalTo(event.getCreationDate()));
        assertThat(result.getPublishedOn(), equalTo(event.getPublishedOn()));
        assertThat(result.getPaid(), equalTo(event.getPaid()));
        assertThat(result.getState(), equalTo(event.getState()));
        assertThat(result.getViews(), equalTo(event.getViews()));
        assertThat(result.getInitiatorId(), equalTo(event.getInitiatorId()));

    }

    @Test
    public void setConfirmedRequestsNumberTest() {
        Event event = makeEventTest();
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));

        ResponseEntity<HttpStatus> httpStatusResponseEntity = eventService.setConfirmedRequestsNumber(ConfirmedRequestsDTO.builder()
                .eventId(1L)
                .count(1)
                .build());

        assertThat(httpStatusResponseEntity, equalTo(ResponseEntity.ok(HttpStatus.OK)));
    }

    @Test
    public void adminEditEventTest() {
        Event event = makeEventTest();
        Event eventFromRepo = makeEventTest();
        eventFromRepo.setState(EventState.PUBLISHED);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(any()))
                .thenReturn(eventFromRepo);

        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();

        Event updatedEvent = eventService.adminEditEvent(1L, adminRequest);

        assertThat(eventFromRepo.getEventId(), equalTo(updatedEvent.getEventId()));
        assertThat(eventFromRepo.getEventDate(), equalTo(updatedEvent.getEventDate()));
        assertThat(eventFromRepo.getTitle(), equalTo(updatedEvent.getTitle()));
        assertThat(eventFromRepo.getAnnotation(), equalTo(updatedEvent.getAnnotation()));
        assertThat(eventFromRepo.getDescription(), equalTo(updatedEvent.getDescription()));
        assertThat(eventFromRepo.getLocation(), equalTo(updatedEvent.getLocation()));
        assertThat(eventFromRepo.getCreationDate(), equalTo(updatedEvent.getCreationDate()));
        assertThat(eventFromRepo.getPublishedOn(), equalTo(updatedEvent.getPublishedOn()));
        assertThat(eventFromRepo.getPaid(), equalTo(updatedEvent.getPaid()));
        assertThat(eventFromRepo.getState(), equalTo(updatedEvent.getState()));
        assertThat(eventFromRepo.getViews(), equalTo(updatedEvent.getViews()));
        assertThat(eventFromRepo.getInitiatorId(), equalTo(updatedEvent.getInitiatorId()));
    }

    @Test
    public void adminEditEventTest_ValidationException_1hoursException_ThrowsException() {
        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();
        Event event = makeEventTest();
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));
        event.setEventDate(LocalDateTime.now().plusHours(1));

        Assertions.assertThrowsExactly(ValidationException.class,
                () -> eventService.adminEditEvent(1L, adminRequest)
        );
    }


    @Test
    public void adminEditEventTest_ValidationException_1hoursBoundaryValues_NotThrowsException() {
        Event event = makeEventTest();
        event.setEventDate(LocalDateTime.now().plusHours(1).plusMinutes(1));
        Event eventFromRepo = makeEventTest();
        eventFromRepo.setState(EventState.PUBLISHED);
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(any()))
                .thenReturn(eventFromRepo);

        RequestUpdateEventAdminDTO adminRequest = makeAdminRequest();

        Event updatedEvent = eventService.adminEditEvent(1L, adminRequest);

        assertThat(eventFromRepo.getEventId(), equalTo(updatedEvent.getEventId()));
        assertThat(eventFromRepo.getEventDate(), equalTo(updatedEvent.getEventDate()));
        assertThat(eventFromRepo.getTitle(), equalTo(updatedEvent.getTitle()));
        assertThat(eventFromRepo.getAnnotation(), equalTo(updatedEvent.getAnnotation()));
        assertThat(eventFromRepo.getDescription(), equalTo(updatedEvent.getDescription()));
        assertThat(eventFromRepo.getLocation(), equalTo(updatedEvent.getLocation()));
        assertThat(eventFromRepo.getCreationDate(), equalTo(updatedEvent.getCreationDate()));
        assertThat(eventFromRepo.getPublishedOn(), equalTo(updatedEvent.getPublishedOn()));
        assertThat(eventFromRepo.getPaid(), equalTo(updatedEvent.getPaid()));
        assertThat(eventFromRepo.getState(), equalTo(updatedEvent.getState()));
        assertThat(eventFromRepo.getViews(), equalTo(updatedEvent.getViews()));
        assertThat(eventFromRepo.getInitiatorId(), equalTo(updatedEvent.getInitiatorId()));
    }

}
