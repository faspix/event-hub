package com.faspix.dto.external;

import java.time.OffsetDateTime;

public record ResponseEventShortDTO(
        Long eventId,
        String title,
        String annotation,
        ResponseCategoryDTO category,
        Integer confirmedRequests,
        OffsetDateTime eventDate,
        ResponseUserShortDTO initiator,
        Boolean paid,
        Long views,
        Integer likes,
        Integer dislikes
){}
