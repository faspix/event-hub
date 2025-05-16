package com.faspix.mapper;

import com.faspix.dto.RequestEventDTO;
import com.faspix.dto.RequestUpdateEventAdminDTO;
import com.faspix.domain.entity.Event;
import com.faspix.domain.index.EventIndex;
import com.faspix.shared.dto.ResponseEventDTO;
import com.faspix.shared.dto.ResponseEventShortDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "categoryId", target = "category.categoryId")
    @Mapping(source = "categoryName", target = "category.name")
    @Mapping(source = "initiatorId", target = "initiator.userId")
    @Mapping(source = "initiatorUsername", target = "initiator.username")
    @Mapping(target = "views", ignore = true)
    ResponseEventDTO eventToResponse(Event event);

    @Mapping(source = "categoryId", target = "category.categoryId")
    @Mapping(source = "categoryName", target = "category.name")
    @Mapping(source = "initiatorId", target = "initiator.userId")
    @Mapping(source = "initiatorUsername", target = "initiator.username")
    @Mapping(target = "views", ignore = true)
    ResponseEventShortDTO eventToShortResponse(Event event);



    @Mapping(source = "paid", target = "paid", defaultValue = "false")
    @Mapping(source = "requestModeration", target = "requestModeration", defaultValue = "false")
    @Mapping(source = "participantLimit", target = "participantLimit", defaultValue = "0")
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "initiatorUsername", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "dislikes", ignore = true)
    Event requestToEvent(RequestEventDTO eventDTO);

    EventIndex eventToIndex(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "dislikes", ignore = true)
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "initiatorUsername", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    void RequestUpdateEventAdminToEvent(@MappingTarget Event event, RequestUpdateEventAdminDTO eventAdminDTO);
}
