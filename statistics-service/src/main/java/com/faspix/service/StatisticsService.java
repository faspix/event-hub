package com.faspix.service;

import com.faspix.shared.dto.RequestEndpointStatsDTO;
import com.faspix.shared.dto.ResponseEndpointStatsDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {

    List<ResponseEndpointStatsDTO> getEndpointStats(LocalDateTime start, LocalDateTime end,
                                                    List<String> uris, Boolean unique);

    void hitEndpoint(RequestEndpointStatsDTO requestDTO);

}
