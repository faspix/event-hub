package com.faspix.client;

import com.faspix.dto.ResponseEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service")
public interface EventServiceClient {

    @GetMapping("/events/{eventId}")
    ResponseEventDTO findEventById(@PathVariable Long eventId);


}
