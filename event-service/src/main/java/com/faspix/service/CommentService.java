package com.faspix.service;

import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;

public interface CommentService {

    ResponseCommentDTO addComment(Long userId, Long eventId, RequestCommentDTO requestDTO);

}
