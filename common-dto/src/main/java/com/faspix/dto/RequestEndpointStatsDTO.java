package com.faspix.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RequestEndpointStatsDTO {

    @NotNull(message = "App shouldn't be null")
    private String app;

    @NotNull(message = "Uri shouldn't be null")
    private String uri;

    @NotNull(message = "IP shouldn't be null")
    private String ip;

    @NotNull(message = "Timestamp shouldn't be null")
    private Instant timestamp;

}
