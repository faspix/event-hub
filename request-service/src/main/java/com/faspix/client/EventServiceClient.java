package com.faspix.client;

import com.faspix.dto.ConfirmedRequestsDTO;
import com.faspix.dto.ResponseEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "event-service")
public interface EventServiceClient {

    @GetMapping("/events/{eventId}")
    ResponseEventDTO findEventById(@PathVariable Long eventId);

}
