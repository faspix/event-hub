package com.faspix.service;

import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;
import com.faspix.entity.Comment;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.UserAlreadyCommentThisEventException;
import com.faspix.mapper.CommentMapper;
import com.faspix.repository.CommentRepository;
import com.faspix.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

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

}
