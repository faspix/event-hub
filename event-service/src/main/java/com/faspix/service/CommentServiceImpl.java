package com.faspix.service;

import com.faspix.client.UserServiceClient;
import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.entity.Comment;
import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.UserAlreadyCommentThisEventException;
import com.faspix.mapper.CommentMapper;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.CommentRepository;
import com.faspix.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final UserMapper userMapper;

    private final EventRepository eventRepository;

    private final UserServiceClient userServiceClient;

    private final CacheManager cacheManager;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseCommentDTO addComment(String userId, Long eventId, RequestCommentDTO requestDTO) {
        if (commentRepository.countCommentsByEventIdAndAuthorId(eventId, userId) > 0)
            throw new UserAlreadyCommentThisEventException("User with id " + userId +
                    " already comment event with id " + eventId);

        ResponseUserShortDTO author = getUserById(userId);

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event with id " + eventId + " not found")
        );
        if (event.getState() != EventState.PUBLISHED)
            throw new EventNotFoundException("Event with id " + eventId + " not published yet");

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
            return Collections.emptyList();

        Set<String> authorIds = comments.stream()
                .map(Comment::getAuthorId)
                .collect(Collectors.toSet());
        Map<String, ResponseUserShortDTO> authorsMap = userServiceClient.getUsersByIds(authorIds)
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

    private ResponseUserShortDTO getUserById(String userId) {
        ResponseUserDTO userDTO;
        Cache cache = cacheManager.getCache("UserService::getUserById");
        if (cache == null) {
            log.error("Cache UserService::getUserById is null, requested userId: {}", userId);
            userDTO = userServiceClient.getUserById(userId);
        } else {
            userDTO = cache.get(userId, ResponseUserDTO.class);
            if (userDTO == null) {
                userDTO = userServiceClient.getUserById(userId);
                log.debug("User with id {} not found in cache, fetching from service", userId);
            }
        }
        return userMapper.responseUserDtoToResponseUserShortDto(userDTO);
    }

}
