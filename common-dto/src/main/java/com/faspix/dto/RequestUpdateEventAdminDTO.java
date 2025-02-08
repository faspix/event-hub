package com.faspix.dto;

import com.faspix.enums.EventStateAction;
import com.faspix.utility.Location;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestUpdateEventAdminDTO {

    private String annotation;

    private Long categoryId;

    private String description;

    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private EventStateAction stateAction;

    private String title;

}
