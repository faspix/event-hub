package com.faspix.dto;

import com.faspix.enums.ParticipationRequestState;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RequestParticipationRequestDTO {

    @NotNull(message = "Request ids shouldn't be null")
    List<Long> requestIds;

    @NotNull(message = "Status shouldn't be null")
    ParticipationRequestState status;

}
