package com.faspix.dto;

import com.faspix.dto.external.ResponseCategoryDTO;
import com.faspix.dto.external.ResponseCommentDTO;
import com.faspix.dto.external.ResponseUserShortDTO;
import com.faspix.dto.external.Location;
import com.faspix.enums.EventState;

import java.time.OffsetDateTime;
import java.util.List;

public record EventsWithCommentsDTO(
        Long eventId,
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
        Integer dislikes,
        List<ResponseCommentDTO> comments
){}
