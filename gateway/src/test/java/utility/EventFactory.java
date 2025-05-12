package utility;

import com.faspix.dto.EventsWithCommentsDTO;
import com.faspix.dto.external.*;
import com.faspix.enums.EventState;

import java.time.OffsetDateTime;
import java.util.List;

import static utility.CommentFactory.makeComment;

public class EventFactory {

    public static ResponseEventDTO makeResponseEvent() {
        return new ResponseEventDTO(
                1L,
                "title",
                "annotation",
                new ResponseCategoryDTO(1L, "name"),
                19,
                OffsetDateTime.MIN,
                "description",
                OffsetDateTime.MIN,
                new ResponseUserShortDTO("1L", "username"),
                new Location(10.0, 13.0),
                true,
                10,
                OffsetDateTime.MIN,
                true,
                EventState.PENDING,
                10L,
                10,
                4
        );
    }

    public static ResponseEventShortDTO makeEventShort() {
        return new ResponseEventShortDTO(
                1L,
                "title",
                "annotation",
                new ResponseCategoryDTO(1L, "name"),
                19,
                OffsetDateTime.MIN,
                new ResponseUserShortDTO("1L", "username"),
                true,
                10L,
                10,
                10

        );
    }


    public static EventsWithCommentsDTO makeEventWithComments() {
        return new EventsWithCommentsDTO(
                1L,
                "title",
                "annotation",
                new ResponseCategoryDTO(1L, "name"),
                19,
                OffsetDateTime.MIN,
                "description",
                OffsetDateTime.MIN,
                new ResponseUserShortDTO("1L", "username"),
                new Location(10.0, 13.0),
                true,
                10,
                OffsetDateTime.MIN,
                true,
                EventState.PENDING,
                10L,
                10,
                4,
                List.of(makeComment())
        );
    }

}
