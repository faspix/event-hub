package com.faspix.client;

import com.faspix.shared.dto.ResponseEndpointStatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "statistics-service",
            fallback = StatisticsServiceClientFallback.class)
public interface StatisticsServiceClient {

    @GetMapping("/statistics/stats")
    List<ResponseEndpointStatsDTO> getEndpointStats(
            @RequestParam("uris") List<String> uris,
            @RequestParam("unique") Boolean unique,
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start
    );

    default List<ResponseEndpointStatsDTO> getStatsById(Long id) {
        return getEndpointStats(List.of("/events/" + id), false, LocalDateTime.now().minusYears(1000));
    }



}
