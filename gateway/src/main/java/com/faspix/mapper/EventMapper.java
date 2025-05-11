package com.faspix.mapper;

import com.faspix.dto.EventsWithCommentsDTO;
import com.faspix.dto.external.ResponseCommentDTO;
import com.faspix.dto.external.ResponseEventDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "comments", source = "comments")
    EventsWithCommentsDTO toEventsWithCommentsDTO(ResponseEventDTO event, List<ResponseCommentDTO> comments);

}
