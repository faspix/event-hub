package com.faspix.service;

import com.faspix.dto.RequestEndpointStatsDTO;
import com.faspix.dto.ResponseEndpointStatsDTO;
import com.faspix.entity.EndpointStats;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public interface StatisticsService {

    List<ResponseEndpointStatsDTO> getEndpointStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    void hitEndpoint(RequestEndpointStatsDTO requestDTO);

}
