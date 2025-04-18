package com.faspix.service;

import com.faspix.config.RabbitMQConfigurationProduce;
import com.faspix.dto.RequestEndpointStatsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
@Slf4j
@RequiredArgsConstructor
public class EndpointStatisticsServiceImpl implements EndpointStatisticsService {

    private final RabbitMQConfigurationProduce configuration;

    private final CacheManager cacheManager;

    @Override
    public void sendEndpointStatistics(RequestEndpointStatsDTO message) {
        configuration.getBus().emitNext(MessageBuilder.withPayload(message).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        log.debug("Message to sent via RabbitMQ: {}", message);
    }
}
