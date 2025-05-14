package com.faspix.mapper;

import com.faspix.entity.EndpointStats;
import com.faspix.shared.dto.RequestEndpointStatsDTO;
import com.faspix.shared.dto.ResponseEndpointStatsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatisticsMapper {

    @Mapping(target = "id", ignore = true)
    EndpointStats RequestToEndpoint(RequestEndpointStatsDTO requestDTO);

    @Mapping(target = "hits", ignore = true)
    ResponseEndpointStatsDTO EndpointToResponse(EndpointStats endpointStats);

}
