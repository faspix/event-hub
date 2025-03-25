package com.faspix.service;

import com.faspix.dto.RequestEndpointStatsDTO;
import com.faspix.dto.ResponseEndpointStatsDTO;
import com.faspix.entity.EndpointStats;
import com.faspix.mapper.StatisticsMapper;
import com.faspix.dao.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticsRepository;

    private final StatisticsMapper statisticsMapper;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<ResponseEndpointStatsDTO> getEndpointStats(LocalDateTime start, LocalDateTime end,
                                                        List<String> uris, Boolean unique) {
        if (start == null)
            start = LocalDateTime.now();
        if (end == null)
            end = LocalDateTime.now().plusYears(1000);

        Instant startInstant = start.toInstant(ZoneOffset.UTC);
        Instant endInstant = end.toInstant(ZoneOffset.UTC);

        return unique ?
                statisticsRepository.findEndpointStatsDistinct(startInstant, endInstant, uris)
                : statisticsRepository.findEndpointStats(startInstant, endInstant, uris);
    }

    // TODO: role
    @Override
    public void hitEndpoint(RequestEndpointStatsDTO requestDTO) {
        EndpointStats endpointStats = statisticsMapper.RequestToEndpoint(requestDTO);
        statisticsRepository.save(endpointStats);
    }
}
