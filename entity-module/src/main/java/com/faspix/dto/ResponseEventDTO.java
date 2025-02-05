package com.faspix.dto;

import com.faspix.utility.EventState;
import com.faspix.utility.Location;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ResponseEventDTO {

    private Long eventId;

    private String title;

    private String annotation;

    // category

    // confirmedRequests

    private OffsetDateTime creationDate;

    private String description;

    private OffsetDateTime eventDate;

    // Initiator

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private OffsetDateTime publishedOn;

    private Boolean requestPreModeration;

    private EventState state;

    //views

}
