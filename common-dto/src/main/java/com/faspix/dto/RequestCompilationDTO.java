package com.faspix.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RequestCompilationDTO {

    @NotBlank(message = "Title shouldn't be blank")
    private String title;

    private Boolean pinned;

    private List<Long> events;

}
