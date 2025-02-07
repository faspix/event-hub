package com.faspix.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
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
