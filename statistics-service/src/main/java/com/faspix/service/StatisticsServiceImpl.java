package com.faspix.service;

import com.faspix.dto.RequestEndpointStatsDTO;
import com.faspix.dto.ResponseEndpointStatsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    @Override
    public ResponseEndpointStatsDTO statsEndpoint(Instant start, Instant end, List<String> uris, Boolean unique) {
        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> hitEndpoint(RequestEndpointStatsDTO requestDTO) {
        return null;
    }
}
