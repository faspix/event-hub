package com.faspix.client;

import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service", fallback = EventServiceClientFallback.class)
public interface EventServiceClient {

    @GetMapping("events/{eventId}")
    ResponseEventDTO getEventById(@PathVariable Long eventId);

}
