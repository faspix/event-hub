package com.faspix.config;

import com.faspix.dto.RequestEndpointStatsDTO;
import com.faspix.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class StreamConsumersConfiguration {

    private final StatisticsService statisticsService;

    @Bean
    Consumer<Message<RequestEndpointStatsDTO>> endpointStatisticsConsume() {
        return msg -> statisticsService.hitEndpoint(msg.getPayload());
    }

}
