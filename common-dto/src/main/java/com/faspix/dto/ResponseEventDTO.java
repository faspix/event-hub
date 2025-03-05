package com.faspix.dto;

import com.faspix.enums.EventState;
import com.faspix.utility.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

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

    private LocalDateTime eventDate;

    private ResponseUserShortDTO initiator;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private OffsetDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private Long views;

    private List<ResponseCommentDTO> comments;

    private Integer likes;

}
