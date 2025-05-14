package com.faspix.shared.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetViewsDTO {

    @NotNull(message = "Event id cannot be null")
    private Long eventId;

    @NotNull(message = "Count of views cannot be null")
    private Long count;

}
