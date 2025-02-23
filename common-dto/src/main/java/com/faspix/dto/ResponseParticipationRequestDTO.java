package com.faspix.dto;

import com.faspix.enums.ParticipationRequestState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
public class ResponseParticipationRequestDTO {

    private Long id;

    private Long eventId;

    private String requesterId;

    private OffsetDateTime creationDate;

    private ParticipationRequestState state;

}
