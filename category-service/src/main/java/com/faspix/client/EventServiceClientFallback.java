package com.faspix.client;

import com.faspix.shared.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventServiceClientFallback implements EventServiceClient {

    @Override
    public boolean isEventsExistsInCategory(Long id) {
        log.error("Error during calling event service for isEventsExistsInCategory, category id: {} ", id);
        throw new ServiceUnavailableException("Error during calling event service");
    }
}
