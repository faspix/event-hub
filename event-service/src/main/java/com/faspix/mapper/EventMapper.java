package com.faspix.mapping;

import com.faspix.dto.ResponseEventDTO;
import com.faspix.entity.Event;
import org.mapstruct.Mapper;

@Mapper
public interface EventMapper {

    ResponseEventDTO eventToResponse(Event event);

}
