package com.faspix.mapper;

import com.faspix.dto.RequestCommentDTO;
import com.faspix.dto.ResponseCommentDTO;
import com.faspix.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    Comment requestToComment(RequestCommentDTO requestDTO);

    @Mapping(target = "author", ignore = true)
    ResponseCommentDTO commentToResponse(Comment comment);

}
