package com.faspix.client;

import com.faspix.dto.ResponseEndpointStatsDTO;
import lombok.extern.slf4j.Slf4j;
import com.faspix.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class StatisticsServiceClientFallback implements StatisticsServiceClient {

    @Override
    public List<ResponseEndpointStatsDTO> getEndpointStats(List<String> uris, Boolean unique, LocalDateTime start) {
        log.error("Error during calling statistics service for getEndpointStats, uris: {}," +
                " unique: {}, start: {} ", uris, unique, start);
        throw new ServiceUnavailableException("Error during calling statistics service");
    }
}
