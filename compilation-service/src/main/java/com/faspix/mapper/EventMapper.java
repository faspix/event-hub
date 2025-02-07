package com.faspix.mapper;

import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    ResponseEventShortDTO eventToShortEvent(ResponseEventDTO eventDTO);

}
