package com.faspix.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmedRequestsDTO {

    @NotNull(message = "Event id cannot be null")
    private Long eventId;

    @NotNull(message = "Count of requests cannot be null")
    private Integer count;

}
