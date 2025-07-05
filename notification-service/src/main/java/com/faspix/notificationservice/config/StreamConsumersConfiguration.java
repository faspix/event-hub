package com.faspix.notificationservice.config;

import com.faspix.notificationservice.router.NotificationRouter;
import com.faspix.notificationservice.service.NotificationService;
import com.faspix.shared.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class StreamConsumersConfiguration {

    private final NotificationRouter notificationRouter;

    @Bean
    Consumer<Message<NotificationDTO>> notificationConsume() {
        return msg ->
                notificationRouter.receiveNotification(msg.getPayload());
    }

}
