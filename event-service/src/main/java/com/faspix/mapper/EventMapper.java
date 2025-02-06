package com.faspix.mapper;

import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EventMapperUtil.class})
public interface EventMapper {

    @Mapping(source = "initiatorId", target = "initiator", qualifiedByName = "getInitiator")
    ResponseEventDTO eventToResponse(Event event);

    @Mapping(source = "initiatorId", target = "initiator", qualifiedByName = "getInitiator")
    ResponseEventShortDTO eventToShortResponse(Event event);

    @Mapping(source = "paid", target = "paid", defaultValue = "false")
    @Mapping(source = "requestModeration", target = "requestModeration", defaultValue = "false")
    @Mapping(source = "participantLimit", target = "participantLimit", defaultValue = "0")
    Event requestToEvent(RequestEventDTO eventDTO);

}
