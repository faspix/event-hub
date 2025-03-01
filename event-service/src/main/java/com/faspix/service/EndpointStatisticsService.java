package com.faspix.service;

import com.faspix.dto.RequestEndpointStatsDTO;

public interface EndpointStatisticsService {

    void sendEndpointStatistics(RequestEndpointStatsDTO message);

}
