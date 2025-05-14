package com.faspix.shared.dto;


import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ResponseEventShortDTO {

    private Long eventId;

    private String title;

    private String annotation;

    private ResponseCategoryDTO category;

    private Integer confirmedRequests;

    private OffsetDateTime eventDate;

    private ResponseUserShortDTO initiator;

    private Boolean paid;

    private Long views;

    private Integer likes;

    private Integer dislikes;

}
