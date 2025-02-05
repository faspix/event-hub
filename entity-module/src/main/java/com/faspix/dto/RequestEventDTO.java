package com.faspix.dto;

import com.faspix.utility.Location;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class RequestEventDTO {

    private String title;

    private String annotation;

    private Integer category;

    private String description;

    private OffsetDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

}
