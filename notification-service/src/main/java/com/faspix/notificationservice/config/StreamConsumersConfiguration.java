package com.faspix.notificationservice.config;

import com.faspix.notificationservice.service.NotificationService;
import com.faspix.shared.dto.ConfirmedRequestNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class StreamConsumersConfiguration {

    private final NotificationService notificationService;

    @Bean
    Consumer<Message<ConfirmedRequestNotificationDTO>> endpointStatisticsConsume() {
        return msg ->
                notificationService.sendConfirmedRequestNotification(msg.getPayload());
    }

}
