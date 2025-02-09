package com.faspix.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResponseEventShortDTO {

    private Long eventId;

    private String title;

    private String annotation;

    private ResponseCategoryDTO category;

    private Integer confirmedRequests;

    private LocalDateTime eventDate;

    private ResponseUserShortDTO initiator;

    private Boolean paid;

    private Integer views;

}
