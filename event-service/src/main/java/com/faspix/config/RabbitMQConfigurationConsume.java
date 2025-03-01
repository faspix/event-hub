package com.faspix.config;

import com.faspix.dto.ConfirmedRequestsDTO;
import com.faspix.dto.RequestEndpointStatsDTO;
import com.faspix.service.EventService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
@Getter
public class RabbitMQConfigurationConsume {

    private final EventService eventService;

    @Bean
    Consumer<Message<ConfirmedRequestsDTO>> newConfirmedRequestConsume() {
        return msg -> eventService.setConfirmedRequestsNumber(msg.getPayload());
    }

}
