package com.faspix.dto.external;

import com.faspix.enums.EventState;

import java.time.OffsetDateTime;

public record ResponseEventDTO(Long eventId,
                               String title,
                               String annotation,
                               ResponseCategoryDTO category,
                               Integer confirmedRequests,
                               OffsetDateTime creationDate,
                               String description,
                               OffsetDateTime eventDate,
                               ResponseUserShortDTO initiator,
                               Location location,
                               Boolean paid,
                               Integer participantLimit,
                               OffsetDateTime publishedAt,
                               Boolean requestModeration,
                               EventState state,
                               Long views,
                               Integer likes,
                               Integer dislikes
) {}
