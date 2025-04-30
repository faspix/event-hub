package com.faspix.service;

import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.enums.EventState;
import com.faspix.utility.EventSortType;

import java.time.OffsetDateTime;
import java.util.List;

public interface SearchService {

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
                                           Integer page,
                                           Integer size
    );

    List<ResponseEventShortDTO> findAllUsersEvents(String userId, Integer page, Integer size);
}
