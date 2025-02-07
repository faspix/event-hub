package com.faspix.dto;

import com.faspix.utility.Location;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Location shouldn't be null")
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

}
