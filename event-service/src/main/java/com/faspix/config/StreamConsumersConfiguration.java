package com.faspix.config;

import com.faspix.dto.ConfirmedRequestsDTO;
import com.faspix.service.EventService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Getter
public class StreamConsumersConfiguration {

    private final EventService eventService;

    @Bean
    Consumer<Message<ConfirmedRequestsDTO>> newConfirmedRequestConsume() {
        return msg -> eventService.setConfirmedRequestsNumber(msg.getPayload());
    }

}
