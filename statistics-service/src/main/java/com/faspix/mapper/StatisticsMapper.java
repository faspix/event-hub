package com.faspix.mapper;

import com.faspix.dto.RequestEndpointStatsDTO;
import com.faspix.dto.ResponseEndpointStatsDTO;
import com.faspix.entity.EndpointStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatisticsMapper {

    @Mapping(target = "id", ignore = true)
    EndpointStats RequestToEndpoint(RequestEndpointStatsDTO requestDTO);

//    @Mapping(target = )
    ResponseEndpointStatsDTO EndpointToResponse(EndpointStats endpointStats);

}
