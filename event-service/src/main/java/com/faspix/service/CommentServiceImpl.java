package com.faspix.service;

import com.faspix.client.UserServiceClient;
import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.entity.Comment;
import com.faspix.entity.Event;
import com.faspix.exception.EventNotFoundException;
import com.faspix.mapper.CommentMapper;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.CommentRepository;
import com.faspix.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final UserMapper userMapper;

    private final EventRepository eventRepository;

    private final UserServiceClient userServiceClient;

    @Override
    public ResponseCommentDTO addComment(Long userId, Long eventId, RequestCommentDTO requestDTO) {
        ResponseUserShortDTO author = userMapper.responseUserDtoToResponseUserShortDto(
                userServiceClient.getUserById(userId)
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event with id " + eventId + " not found")
        );

        Comment comment = commentMapper.requestToComment(requestDTO);
        comment.setAuthorId(userId);
        comment.setEvent(event);
        comment.setCreationDate(Instant.now());

        Comment savedComment = commentRepository.save(comment);

        ResponseCommentDTO responseDTO = commentMapper.commentToResponse(savedComment);
        responseDTO.setAuthor(author);
        return responseDTO;
    }

    @Override
    public List<ResponseCommentDTO> findCommentsByEventId(Long eventId) {
        List<Comment> comments = commentRepository.findCommentsByEventId(eventId);
        if (comments.isEmpty())
            return null;

        Set<Long> authorIds = comments.stream()
                .map(Comment::getAuthorId)
                .collect(Collectors.toSet());
        Map<Long, ResponseUserShortDTO> authorsMap = userServiceClient.getUsersByIds(authorIds)
                .stream()
                .collect(Collectors.toMap(ResponseUserShortDTO::getUserId, user -> user));

        return comments.stream()
                .map(comment -> {
                    ResponseCommentDTO responseDTO = commentMapper.commentToResponse(comment);
                    responseDTO.setAuthor(authorsMap.getOrDefault(comment.getAuthorId(), null));
                    return responseDTO;
                })
                .toList();
    }
}
