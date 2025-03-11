package com.faspix.client;

import com.faspix.dto.ResponseEventShortDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "event-service", fallback = EventServiceClientFallback.class)
public interface EventServiceClient {

    @PostMapping("/events/batch")
    List<ResponseEventShortDTO> getEventsByIds(@RequestBody List<Long> ids);

}
