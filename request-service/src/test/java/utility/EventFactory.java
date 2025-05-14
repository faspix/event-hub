package utility;

import com.faspix.shared.dto.ResponseCategoryDTO;
import com.faspix.shared.dto.ResponseEventDTO;
import com.faspix.shared.dto.ResponseEventShortDTO;
import com.faspix.shared.dto.ResponseUserShortDTO;
import com.faspix.shared.utility.EventState;
import com.faspix.shared.utility.Location;

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
                .publishedAt(OffsetDateTime.now())
                .requestModeration(false)
                .state(EventState.PENDING)
                .views(0L)
                .build();
    }
}
