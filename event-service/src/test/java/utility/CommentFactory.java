package utility;

import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;
import com.faspix.entity.Comment;

import java.time.Instant;

import static utility.EventFactory.*;
import static utility.UserFactory.*;

public class CommentFactory {

    public static Comment makeComment() {
        return Comment.builder()
                .id(null)
                .authorId("1")
                .creationDate(Instant.now())
                .event(makeEventTest())
                .text("Comment text")
                .build();
    }

    public static ResponseCommentDTO makeResponseComment() {
        return ResponseCommentDTO.builder()
                .id(1L)
                .author(makeResponseShortUser())
                .text("Comment text")
                .build();
    }

    public static RequestCommentDTO makeRequestComment() {
        return RequestCommentDTO.builder()
                .text("Comment text")
                .build();
    }

}
