package com.faspix.service;

import com.faspix.dto.RequestEndpointStatsDTO;
import com.faspix.dto.ResponseEndpointStatsDTO;
import com.faspix.entity.EndpointStats;
import com.faspix.mapper.StatisticsMapper;
import com.faspix.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticsRepository;

    private final StatisticsMapper statisticsMapper;

    @Override
    public List<ResponseEndpointStatsDTO> statsEndpoint(LocalDateTime start, LocalDateTime end,
                                                        List<String> uris, Boolean unique) {
        Instant startInstant = start.toInstant(ZoneOffset.UTC);
        Instant endInstant = end.toInstant(ZoneOffset.UTC);

        return unique ?
                statisticsRepository.findEndpointStatsDistinct(startInstant, endInstant, uris)
                : statisticsRepository.findEndpointStats(startInstant, endInstant, uris);
    }

    @Override // TODO: return value
    public void hitEndpoint(RequestEndpointStatsDTO requestDTO) {
        EndpointStats endpointStats = statisticsMapper.RequestToEndpoint(requestDTO);
        statisticsRepository.save(endpointStats);
    }
}
