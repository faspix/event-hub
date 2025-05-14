package com.faspix.config;

import com.faspix.service.EventService;
import com.faspix.shared.dto.ConfirmedRequestsDTO;
import com.faspix.shared.dto.UpdateCategoryNameDTO;
import com.faspix.shared.dto.UpdateUsernameDTO;
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
