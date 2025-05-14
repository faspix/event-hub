package com.faspix.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "event-service", fallback = EventServiceClientFallback.class)
public interface EventServiceClient {

    @GetMapping("/events/categories/exists")
    boolean isEventsExistsInCategory(@RequestParam Long id);

}
