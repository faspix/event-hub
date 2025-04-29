package com.faspix.service;

import com.faspix.client.StatisticsServiceClient;
import com.faspix.dto.ResponseEndpointStatsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventViewService {

    private final CacheManager cacheManager;

    private final StatisticsServiceClient statisticsServiceClient;

    public Long getViewsByEventId(Long eventId) {
        Cache cache = cacheManager.getCache("EventService::getEventViewsById");
        if (cache == null) {
            log.error("Cache EventService::getEventViewsById is null, requested eventId id: {}", eventId);
            return fetchEventViewsFromDB(eventId);
        }

        Long views = cache.get(eventId, Long.class);
        if (views != null) {
            return views;
        }

        log.debug("Views for event with id {} not found in cache, fetching from statistics service", eventId);
        return fetchEventViewsFromDB(eventId);
    }

    private Long fetchEventViewsFromDB(Long eventId) {
        List<ResponseEndpointStatsDTO> statsById = statisticsServiceClient.getStatsById(eventId);
        return (statsById == null || statsById.isEmpty())
                ? 0
                : statsById.getFirst().getHits();
    }

}
