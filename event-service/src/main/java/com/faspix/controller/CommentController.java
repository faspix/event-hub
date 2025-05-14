package com.faspix.controller;

import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;
import com.faspix.service.CommentService;
import com.faspix.domain.enums.CommentSortType;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCommentDTO addComment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long eventId,
            @RequestBody RequestCommentDTO requestDTO
    ) {
        String username = jwt.getClaim("username");
        return commentService.addComment(jwt.getSubject(), username, eventId, requestDTO);
    }

    @GetMapping("/comments/{eventId}")
    @Hidden
    public List<ResponseCommentDTO> getCommentsByEventId(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "DESC") CommentSortType sortType
            ) {
        return commentService.findCommentsByEventId(eventId, sortType, from, size);
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseCommentDTO editComment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long commentId,
            @RequestBody RequestCommentDTO requestDTO
    ) {
        return commentService.editComment(jwt.getSubject(), commentId, requestDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(jwt.getSubject(), commentId);
    }

}
