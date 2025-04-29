package com.faspix.mapper;

import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.RequestUpdateEventAdminDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.entity.Event;
import com.faspix.entity.EventIndex;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ResponseEventDTO eventToResponse(Event event);

    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    ResponseEventShortDTO eventToShortResponse(Event event);

    @Mapping(source = "paid", target = "paid", defaultValue = "false")
    @Mapping(source = "requestModeration", target = "requestModeration", defaultValue = "false")
    @Mapping(source = "participantLimit", target = "participantLimit", defaultValue = "0")
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "dislikes", ignore = true)
    Event requestToEvent(RequestEventDTO eventDTO);

    EventIndex eventToIndex(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void RequestUpdateEventAdminToEvent(@MappingTarget Event event, RequestUpdateEventAdminDTO eventAdminDTO);
}
