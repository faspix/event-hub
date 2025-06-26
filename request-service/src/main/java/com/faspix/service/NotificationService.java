package com.faspix.service;

import com.faspix.config.StreamProducersConfiguration;
import com.faspix.shared.dto.ConfirmedRequestNotificationDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Sinks;

@AllArgsConstructor
@Component
@Slf4j
public class NotificationService {

    private final StreamProducersConfiguration configuration;

    public void sendNotification(ConfirmedRequestNotificationDTO message) {
        configuration.getNotificationBus().emitNext(MessageBuilder.withPayload(message).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        log.debug("Message to sent via Kafka: {}", message);
    }

}
