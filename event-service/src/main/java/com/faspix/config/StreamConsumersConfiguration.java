package com.faspix.config;

import com.faspix.dto.ConfirmedRequestsDTO;
import com.faspix.dto.UpdateCategoryNameDTO;
import com.faspix.dto.UpdateUsernameDTO;
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

    @Bean
    Consumer<Message<UpdateUsernameDTO>> updateUsernameConsume() {
        return msg -> eventService.updateInitiatorUsername(msg.getPayload());
    }

    @Bean
    Consumer<Message<UpdateCategoryNameDTO>> updateCategoryNameConsume() {
        return msg -> eventService.updateCategoryName(msg.getPayload());
    }

}
