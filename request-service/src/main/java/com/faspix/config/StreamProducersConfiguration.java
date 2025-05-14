package com.faspix.config;

import com.faspix.shared.dto.ConfirmedRequestsDTO;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.function.Supplier;

@Configuration
@Getter
public class StreamProducersConfiguration {

    private final Sinks.Many<Message<ConfirmedRequestsDTO>> bus = Sinks.many()
            .multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    @Bean
    Supplier<Flux<Message<ConfirmedRequestsDTO>>> newConfirmedRequestProduce() {
        return bus::asFlux;
    }

}
