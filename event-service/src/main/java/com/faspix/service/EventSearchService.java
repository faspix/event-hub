package com.faspix.service;

import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.enums.EventState;
import com.faspix.utility.EventSortType;
import jakarta.servlet.http.HttpServletRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public interface EventSearchService {

    List<ResponseEventShortDTO> findEvents(String text,
                                           List<Long> categories,
                                           Boolean paid,
                                           OffsetDateTime rangeStart,
                                           OffsetDateTime rangeEnd,
                                           Boolean onlyAvailable,
                                           EventSortType sort,
                                           Integer from,
                                           Integer size
    );


    List<ResponseEventDTO> findEventsAdmin(List<String> users,
                                           List<EventState> states,
                                           List<Long> categories,
                                           OffsetDateTime rangeStart,
                                           OffsetDateTime rangeEnd,
                                           Integer from,
                                           Integer size
    );

    List<ResponseEventShortDTO> findAllUsersEvents(String userId, Integer from, Integer size);

    ResponseEventDTO findEventById(Long eventId, HttpServletRequest httpServletRequest);

    boolean isEventExists(Long eventId);

    boolean isEventsExistsInCategory(Long categoryId);

    List<ResponseEventShortDTO> findEventsByIds(Set<Long> ids, int from, int size);

}
