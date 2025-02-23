package com.faspix.controller;

import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;
import com.faspix.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCommentDTO addComment(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long eventId,
            @RequestBody RequestCommentDTO requestDTO
    ) {
        return commentService.addComment(userId, eventId, requestDTO);
    }

}
