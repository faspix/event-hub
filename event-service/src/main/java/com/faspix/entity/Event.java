package com.faspix.entity;

import com.faspix.enums.EventState;
import com.faspix.utility.Location;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String title;

    private String annotation;

    // category

    // confirmedRequests

    private OffsetDateTime creationDate;

    private String description;

    private OffsetDateTime eventDate;

    // Initiator

    @Embedded
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private OffsetDateTime publishedOn;

    private Boolean requestPreModeration;

    private EventState state;

    //views

}
