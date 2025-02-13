package com.faspix.service;

import com.faspix.client.UserServiceClient;
import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.entity.Comment;
import com.faspix.entity.Event;
import com.faspix.mapper.CommentMapper;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final UserMapper userMapper;

    private final EventService eventService;

    private final UserServiceClient userServiceClient;

    @Override
    public ResponseCommentDTO addComment(Long userId, Long eventId, RequestCommentDTO requestDTO) {
        ResponseUserShortDTO author = userMapper.responseUserDtoToResponseUserShortDto(
                userServiceClient.getUserById(userId)
        );
        Event event = eventService.getEventById(eventId);

        Comment comment = commentMapper.requestToComment(requestDTO);
        comment.setAuthorId(author.getUserId());
        comment.setEvent(event);
        comment.setCreationDate(Instant.now());

        Comment savedComment = commentRepository.save(comment);

        ResponseCommentDTO responseDTO = commentMapper.commentToResponse(savedComment);
        responseDTO.setAuthor(author);

        return responseDTO;
    }
}
