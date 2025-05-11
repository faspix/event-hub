package com.faspix.dto;

import com.faspix.dto.external.ResponseCategoryDTO;
import com.faspix.dto.external.ResponseCommentDTO;
import com.faspix.dto.external.ResponseUserShortDTO;
import com.faspix.dto.external.Location;
import com.faspix.enums.EventState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventsWithCommentsDTO {

    private Long eventId;

    private String title;

    private String annotation;

    private ResponseCategoryDTO category;

    private Integer confirmedRequests;

    private OffsetDateTime creationDate;

    private String description;

    private OffsetDateTime eventDate;

    private ResponseUserShortDTO initiator;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private OffsetDateTime publishedAt;

    private Boolean requestModeration;

    private EventState state;

    private Long views;

    private Integer likes;

    private Integer dislikes;

    private List<ResponseCommentDTO> comments;

}
