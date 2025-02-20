package com.faspix.client;

import com.faspix.dto.ResponseEventDTO;
import com.faspix.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventServiceClientFallback implements EventServiceClient {
    @Override
    public ResponseEventDTO findEventById(Long eventId) {
        log.error("Error during calling event service for findEventById, category id: {} ", eventId);
        throw new ServiceUnavailableException("Error during calling event service");
    }
}
