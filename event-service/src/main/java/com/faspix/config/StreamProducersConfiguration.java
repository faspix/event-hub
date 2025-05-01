package com.faspix.config;

import com.faspix.dto.RequestEndpointStatsDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
@Getter
public class StreamProducersConfiguration {

    private final Sinks.Many<Message<RequestEndpointStatsDTO>> bus = Sinks.many()
            .multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    @Bean
    Supplier<Flux<Message<RequestEndpointStatsDTO>>> endpointStatisticsProduce() {
        return bus::asFlux;
    }

}
