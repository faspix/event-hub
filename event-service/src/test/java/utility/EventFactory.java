package utility;

import com.faspix.dto.*;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.enums.EventStateAction;
import com.faspix.utility.Location;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static utility.UserFactory.makeResponseShortUser;

public class EventFactory {



    public static Event makeEventTest() {
        return Event.builder()
                .eventId(null)
                .title("title")
                .annotation("annotation")
                .categoryId(1L)
                .participantLimit(10)
                .creationDate(OffsetDateTime.now())
                .description("description")
                .eventDate(LocalDateTime.MAX)
                .initiatorId("1")
                .location(new Location(10.0, 10.0))
                .paid(false)
                .confirmedRequests(10)
                .publishedOn(OffsetDateTime.now())
                .requestModeration(false)
                .state(EventState.PENDING)
                .views(0)
                .build();
    }

    public static ResponseEventDTO makeResponseEventTest() {
        return ResponseEventDTO.builder()
                .eventId(1L)
                .title("title")
                .annotation("annotation")
                .category(ResponseCategoryDTO.builder()
                        .categoryId(1L)
                        .name("category")
                        .build())
                .participantLimit(2)
                .creationDate(OffsetDateTime.now())
                .description("description")
                .eventDate(LocalDateTime.MAX)
                .initiator(makeResponseShortUser())
                .location(new Location(10.0, 10.0))
                .paid(false)
                .confirmedRequests(10)
                .publishedOn(OffsetDateTime.now())
                .requestModeration(false)
                .state(EventState.PENDING)
                .views(0)
                .build();
    }

    public static ResponseEventShortDTO makeShortResponseEventTest() {
        return ResponseEventShortDTO.builder()
                .eventId(1L)
                .title("title")
                .annotation("annotation")
                .category(ResponseCategoryDTO.builder()
                        .categoryId(1L)
                        .name("category")
                        .build())
                .confirmedRequests(2)
                .eventDate(LocalDateTime.MAX)
                .initiator(makeResponseShortUser())
                .paid(false)
                .views(0)
                .build();
    }

    public static RequestEventDTO makeRequestEventTest() {
        return RequestEventDTO.builder()
                .title("title")
                .annotation("annotation")
                .categoryId(1L)
                .description("description")
                .eventDate(LocalDateTime.MAX)
                .location(new Location(10.0, 10.0))
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .build();
    }

    public static RequestUpdateEventAdminDTO makeAdminRequest() {
        return RequestUpdateEventAdminDTO.builder()
                .annotation("annotation")
                .categoryId(1L)
                .description("description")
                .eventDate(LocalDateTime.MAX)
                .location(new Location(10.0, 10.0))
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .stateAction(EventStateAction.PUBLISH_EVENT)
                .title("title")
                .build();
    }
}
