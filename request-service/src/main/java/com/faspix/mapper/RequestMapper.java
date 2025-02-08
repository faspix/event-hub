package com.faspix.mapper;

import com.faspix.dto.ResponseParticipationRequestDTO;
import com.faspix.entity.Request;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    ResponseParticipationRequestDTO participationRequestToResponse(Request request);

}
