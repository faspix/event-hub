package com.faspix.service;

import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;
import com.faspix.utility.CommentSortType;

import java.util.List;

public interface CommentService {

    ResponseCommentDTO addComment(String userId, String username, Long eventId, RequestCommentDTO requestDTO);

    List<ResponseCommentDTO> findCommentsByEventId(Long eventId, CommentSortType sortType, Integer from, Integer size);

    ResponseCommentDTO editComment(String userId, Long commentId, RequestCommentDTO requestDTO);

    void deleteComment(String userId, Long commentId);

}
