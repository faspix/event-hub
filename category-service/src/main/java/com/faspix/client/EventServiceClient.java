package com.faspix.client;

import com.faspix.dto.ResponseEventShortDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "event-service")
public interface EventServiceClient {

    @GetMapping("/events/categories/{catId}")
    List<ResponseEventShortDTO> findEventsByCategoryId(@PathVariable Long catId);

}
