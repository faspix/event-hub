package com.faspix.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ConfirmedRequestsDTO {

    @NotNull(message = "Event id cannot be null")
    private Long eventId;

    @NotNull(message = "Count of requests cannot be null")
    private Integer count;

}
