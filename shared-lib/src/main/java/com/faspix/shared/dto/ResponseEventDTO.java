package com.faspix.shared.dto;

import com.faspix.shared.utility.EventState;
import com.faspix.shared.utility.Location;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ResponseEventDTO {

    private Long eventId;

    private String title;

    private String annotation;

    private ResponseCategoryDTO category;

    private Integer confirmedRequests;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

}
