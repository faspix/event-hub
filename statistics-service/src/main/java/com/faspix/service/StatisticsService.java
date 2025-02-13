package com.faspix.service;

import com.faspix.dto.RequestEndpointStatsDTO;
import com.faspix.dto.ResponseEndpointStatsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

public interface StatisticsService {
    ResponseEndpointStatsDTO statsEndpoint(Instant start, Instant end, List<String> uris, Boolean unique);

    ResponseEntity<HttpStatus> hitEndpoint(RequestEndpointStatsDTO requestDTO);

}
