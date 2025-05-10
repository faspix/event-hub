package com.faspix.controller;

import com.faspix.service.SearchService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Hidden
public class InternalEventController {

    private final SearchService searchService;

    @GetMapping("/exists")
    public boolean isEventExists(
            @RequestParam Long id
    ) {
       return searchService.isEventExists(id);
    }

    @GetMapping("/categories/exists")
    public boolean isEventsExistsInCategory(
            @RequestParam Long id
    ) {
        return searchService.isEventsExistsInCategory(id);
    }

}
