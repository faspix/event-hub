package com.faspix.controller;

import com.faspix.dto.RequestEndpointStatsDTO;
import com.faspix.dto.ResponseEndpointStatsDTO;
import com.faspix.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hitEndpoint(
            @RequestBody RequestEndpointStatsDTO requestDTO
    ) {
        statisticsService.hitEndpoint(requestDTO);
    }

    @GetMapping("/stats")
    public List<ResponseEndpointStatsDTO> getEndpointStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
    ) {
        return statisticsService.getEndpointStats(start, end, uris, unique);
    }

}
