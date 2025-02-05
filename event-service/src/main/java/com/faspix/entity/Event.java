package com.faspix.entity;

import com.faspix.utility.EventState;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
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
