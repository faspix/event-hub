package com.faspix.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCompilationDTO {

    @NotBlank(message = "Title shouldn't be blank")
    private String title;

    private Boolean pinned;

    private List<Long> events;

}
