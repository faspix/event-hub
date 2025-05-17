package com.faspix.service;

import com.faspix.client.StatisticsServiceClient;
import com.faspix.shared.dto.ResponseEndpointStatsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        log.debug("Cache miss for eventId {}. Fetching from statistics service.", eventId);
        Long fetchedViews = fetchEventViewsFromDB(eventId);
        cache.put(eventId, fetchedViews);
        return fetchedViews;
    }

    private Long fetchEventViewsFromDB(Long eventId) {
        List<ResponseEndpointStatsDTO> stats = statisticsServiceClient.getStatsById(eventId);
        return Optional.ofNullable(stats)
                .filter(list -> !list.isEmpty())
                .map(list -> list.getFirst().getHits())
                .orElse(0L);
    }

}
