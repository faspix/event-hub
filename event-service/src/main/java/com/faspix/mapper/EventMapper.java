package com.faspix.mapper;

import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    ResponseEventDTO eventToResponse(Event event);

    Event responseToEvent(ResponseEventDTO responseDTO);

    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    ResponseEventShortDTO eventToShortResponse(Event event);

    @Mapping(source = "paid", target = "paid", defaultValue = "false")
    @Mapping(source = "requestModeration", target = "requestModeration", defaultValue = "false")
    @Mapping(source = "participantLimit", target = "participantLimit", defaultValue = "0")
    Event requestToEvent(RequestEventDTO eventDTO);

}
