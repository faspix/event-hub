package com.faspix.dto;

import com.faspix.shared.utility.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class RequestEventDTO {

    @NotBlank(message = "Title shouldn't be blank")
    private String title;

    @NotBlank(message = "Annotation shouldn't be blank")
    private String annotation;

    @NotNull(message = "Category shouldn't be null")
    private Long categoryId;

    @NotNull(message = "Description shouldn't be null")
    private String description;

    @NotNull(message = "Event date shouldn't be null")
    private OffsetDateTime eventDate;

    @NotNull(message = "Location shouldn't be null")
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

}
