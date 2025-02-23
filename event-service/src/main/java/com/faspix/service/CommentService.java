package com.faspix.service;

import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;

import java.util.List;

public interface CommentService {

    ResponseCommentDTO addComment(String userId, Long eventId, RequestCommentDTO requestDTO);

    List<ResponseCommentDTO> findCommentsByEventId(Long eventId);

}
