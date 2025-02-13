package com.faspix.controller;

import com.faspix.dto.RequestEndpointStatsDTO;
import com.faspix.dto.ResponseEndpointStatsDTO;
import com.faspix.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<HttpStatus> hitEndpoint(
            @RequestBody RequestEndpointStatsDTO requestDTO
    ) {
        return statisticsService.hitEndpoint(requestDTO);
    }

    @GetMapping("/stats")
    public ResponseEndpointStatsDTO statsEndpoint(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Instant start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Instant end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
    ) {
        return statisticsService.statsEndpoint(start, end, uris, unique);
    }

}
