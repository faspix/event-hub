package com.faspix.service;

import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;
import com.faspix.entity.Comment;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.exception.CommentNotFoundException;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.UserAlreadyCommentThisEventException;
import com.faspix.exception.ValidationException;
import com.faspix.mapper.CommentMapper;
import com.faspix.repository.CommentRepository;
import com.faspix.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseCommentDTO addComment(String userId, String username, Long eventId, RequestCommentDTO requestDTO) {
        if (commentRepository.countCommentsByEventIdAndAuthorId(eventId, userId) > 0)
            throw new UserAlreadyCommentThisEventException("User with id " + userId +
                    " already comment event with id " + eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event with id " + eventId + " not found")
        );
        if (event.getState() != EventState.PUBLISHED)
            throw new EventNotFoundException("Event with id " + eventId + " not published yet");

        Comment comment = commentMapper.requestToComment(requestDTO);
        comment.setAuthorId(userId);
        comment.setAuthorUsername(username);
        comment.setEvent(event);

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.commentToResponse(savedComment);
    }

    @Override
    public List<ResponseCommentDTO> findCommentsByEventId(Long eventId, Integer from, Integer size) {
        List<Comment> comments = commentRepository.findByEvent_EventId(eventId);
        if (comments.isEmpty())
            return Collections.emptyList();

        return comments.stream()
                .map(commentMapper::commentToResponse)
                .toList();
    }

    @Transactional
    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseCommentDTO editComment(String userId, Long commentId, RequestCommentDTO requestDTO) {
        Comment comment = getCommentById(commentId);
        checkCommentAuthority(userId, comment);
        return commentMapper.commentToResponse(
                commentRepository.save(comment)
        );
    }

    @Transactional
    @Override
    public void deleteComment(String userId, Long commentId) {
        Comment comment = getCommentById(commentId);
        checkCommentAuthority(userId, comment);
        commentRepository.delete(comment);
    }

    private void checkCommentAuthority(String userId, Comment comment) {
        boolean isAuthor = Objects.equals(comment.getAuthorId(), userId);

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAuthor && !isAdmin) {
            throw new ValidationException("User with id " + userId + " is not authorized " +
                    "to edit comment with id " + comment.getId());
        }
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException("Comment with id " + commentId + " not found")
        );
    }

}
