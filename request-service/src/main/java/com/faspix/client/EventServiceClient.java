package com.faspix.client;

import com.faspix.shared.dto.ResponseEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "event-service", fallback = EventServiceClientFallback.class)
public interface EventServiceClient {

    @GetMapping("/events/{eventId}")
    ResponseEventDTO findEventById(@PathVariable Long eventId);

}
