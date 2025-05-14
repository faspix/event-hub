package com.faspix.service;

import com.faspix.shared.dto.RequestEndpointStatsDTO;

public interface EndpointStatisticsService {

    void sendEndpointStatistics(RequestEndpointStatsDTO message);

}
