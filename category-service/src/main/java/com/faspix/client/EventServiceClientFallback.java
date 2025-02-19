package com.faspix.client;

import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class EventServiceClientFallback implements EventServiceClient {
    @Override
    public List<ResponseEventShortDTO> findEventsByCategoryId(Long catId) {
        log.error("Error during calling user service for findEventsByCategoryId, category id: {} ", catId);
        throw new ServiceUnavailableException("Error during calling event service");
    }
}
