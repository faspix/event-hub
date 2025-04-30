package utility;

import com.faspix.dto.*;
import com.faspix.entity.Event;
import com.faspix.entity.EventIndex;
import com.faspix.enums.EventState;
import com.faspix.enums.EventStateAction;
import com.faspix.utility.Location;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static utility.UserFactory.makeResponseShortUser;

public class EventFactory {

    public static EventIndex makeEventIndex() {
        return new EventIndex(
                1L,
                "title",
                "annotation",
                1L,
                OffsetDateTime.now().plusYears(1000),
                false,
                EventState.PUBLISHED,
                2,
                1
        );
    }

    public static Event makeEventTest() {
        return Event.builder()
                .eventId(null)
                .title("title")
                .annotation("annotation")
                .categoryId(1L)
                .categoryName("category")
                .initiatorUsername("initiator_username")
                .participantLimit(10)
                .creationDate(OffsetDateTime.now())
                .description("description")
                .eventDate(OffsetDateTime.now().plusYears(1000))
                .initiatorId("1")
                .location(new Location(10.0, 10.0))
                .paid(false)
                .confirmedRequests(10)
                .publishedAt(OffsetDateTime.now())
                .requestModeration(false)
                .state(EventState.PENDING)
                .likes(0)
                .dislikes(0)
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
                .eventDate(OffsetDateTime.now().plusYears(1000))
                .initiator(makeResponseShortUser())
                .location(new Location(10.0, 10.0))
                .paid(false)
                .confirmedRequests(10)
                .publishedAt(OffsetDateTime.now())
                .requestModeration(false)
                .state(EventState.PENDING)
                .views(0L)
                .likes(0)
                .dislikes(0)
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
                .eventDate(OffsetDateTime.now().plusYears(1000))
                .initiator(makeResponseShortUser())
                .paid(false)
                .views(0L)
                .likes(0)
                .dislikes(0)
                .build();
    }

    public static RequestEventDTO makeRequestEventTest() {
        return RequestEventDTO.builder()
                .title("title")
                .annotation("annotation")
                .categoryId(1L)
                .description("description")
                .eventDate(OffsetDateTime.now().plusYears(1000))
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
                .eventDate(OffsetDateTime.now().plusYears(1000))
                .location(new Location(10.0, 10.0))
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .stateAction(EventStateAction.PUBLISH_EVENT)
                .title("title")
                .build();
    }
}
