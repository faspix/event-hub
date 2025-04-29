package utility;

import com.faspix.dto.*;
import com.faspix.enums.EventState;
import com.faspix.enums.EventStateAction;
import com.faspix.utility.Location;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class EventFactory {

    public static ResponseUserShortDTO makeResponseShortUser() {
        return ResponseUserShortDTO.builder()
                .userId("1")
                .username("name")
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
                .eventDate(OffsetDateTime.MAX)
                .initiator(makeResponseShortUser())
                .location(new Location(10.0, 10.0))
                .paid(false)
                .confirmedRequests(10)
                .publishedOn(OffsetDateTime.now())
                .requestModeration(false)
                .state(EventState.PENDING)
                .views(0L)
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
                .eventDate(OffsetDateTime.MAX)
                .initiator(makeResponseShortUser())
                .paid(false)
                .views(0L)
                .build();
    }

    public static RequestEventDTO makeRequestEventTest() {
        return RequestEventDTO.builder()
                .title("title")
                .annotation("annotation")
                .categoryId(1L)
                .description("description")
                .eventDate(OffsetDateTime.MAX)
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
                .eventDate(OffsetDateTime.MAX)
                .location(new Location(10.0, 10.0))
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .stateAction(EventStateAction.PUBLISH_EVENT)
                .title("title")
                .build();
    }
}
