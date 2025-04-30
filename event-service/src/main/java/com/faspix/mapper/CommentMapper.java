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
    @Mapping(target = "authorUsername", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comment requestToComment(RequestCommentDTO requestDTO);

    @Mapping(target = "author.userId", source = "authorId")
    @Mapping(target = "author.username", source = "authorUsername")
    ResponseCommentDTO commentToResponse(Comment comment);

}
