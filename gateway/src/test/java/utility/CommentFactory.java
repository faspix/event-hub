package utility;

import com.faspix.dto.external.ResponseCommentDTO;
import com.faspix.dto.external.ResponseUserShortDTO;

import java.time.Instant;

public class CommentFactory {

    public static ResponseCommentDTO makeComment() {
        return new ResponseCommentDTO(
                1L,
                new ResponseUserShortDTO("1", "username"),
                "comment text",
                Instant.now(),
                Instant.now()
        );
    }

}
