package com.faspix.dto;

import com.faspix.enums.EventStateAction;
import com.faspix.utility.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
public class RequestUpdateEventAdminDTO {

    private String annotation;

    private Long categoryId;

    private String description;

    private OffsetDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private EventStateAction stateAction;

    private String title;

}
