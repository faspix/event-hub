package com.faspix.entity;

import com.faspix.enums.EventState;
import com.faspix.utility.Location;
import com.faspix.dto.ResponseUserShortDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
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

    private Integer confirmedRequests;

    private OffsetDateTime creationDate;

    private String description;

    private LocalDateTime eventDate;

    private Long initiatorId;

    @Embedded
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private OffsetDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private Integer views;

}
