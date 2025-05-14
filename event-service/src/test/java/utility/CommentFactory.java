package utility;

import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;
import com.faspix.domain.entity.Comment;

import java.time.Instant;

import static utility.EventFactory.*;
import static utility.UserFactory.*;

public class CommentFactory {

    public static Comment makeComment() {
        return Comment.builder()
                .id(null)
                .authorId("1")
                .createdAt(Instant.now())
                .authorUsername("user")
                .event(makeEventTest())
                .text("Comment text")
                .build();
    }

    public static ResponseCommentDTO makeResponseComment() {
        return ResponseCommentDTO.builder()
                .id(1L)
                .author(makeResponseShortUser())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .text("Comment text")
                .build();
    }

    public static RequestCommentDTO makeRequestComment() {
        return RequestCommentDTO.builder()
                .text("Comment text")
                .build();
    }

}
