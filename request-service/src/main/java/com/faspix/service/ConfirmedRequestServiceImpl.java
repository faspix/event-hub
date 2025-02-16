package com.faspix.service;

import com.faspix.config.RabbitMQConfiguration;
import com.faspix.dto.ConfirmedRequestsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfirmedRequestServiceImpl implements ConfirmedRequestService {

    private final RabbitMQConfiguration configuration;

    @Override
    public void sendConfirmedRequestMsg(ConfirmedRequestsDTO message) {
        configuration.getBus().emitNext(MessageBuilder.withPayload(message).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        log.debug("Message to sent via RabbitMQ: {}", message);
    }

}
