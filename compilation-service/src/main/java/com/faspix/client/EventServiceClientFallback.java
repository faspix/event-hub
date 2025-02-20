package com.faspix.client;

import com.faspix.dto.ResponseEventDTO;
import com.faspix.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class EventServiceClientFallback implements EventServiceClient {
    @Override
    public ResponseEventDTO getEventById(Long eventId) {
        log.error("Error during calling event service for getEventById, event id: {} ", eventId);
        throw new ServiceUnavailableException("Error during calling event service");
    }
}
