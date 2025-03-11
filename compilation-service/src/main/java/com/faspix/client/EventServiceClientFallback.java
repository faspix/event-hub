package com.faspix.client;

import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class EventServiceClientFallback implements EventServiceClient {

    @Override
    public List<ResponseEventShortDTO> getEventsByIds(List<Long> ids) {
        log.error("Error during calling event service for getEventById, event ids: {} ", ids);
        throw new ServiceUnavailableException("Error during calling event service");
    }
}
