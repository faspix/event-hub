package com.faspix.service;

import com.faspix.config.StreamProducersConfiguration;
import com.faspix.dto.UpdateCategoryNameDTO;
import com.faspix.dto.UpdateUsernameDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateCategoryNameService {

    private final StreamProducersConfiguration configuration;

    public void sendUpdateCategoryName(UpdateCategoryNameDTO message) {
        configuration.getBus().emitNext(MessageBuilder.withPayload(message).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        log.debug("Message to sent via Kafka: {}", message);
    }

}
