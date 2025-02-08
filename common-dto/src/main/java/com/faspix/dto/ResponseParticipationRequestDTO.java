package com.faspix.dto;

import com.faspix.enums.ParticipationRequestState;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ResponseParticipationRequestDTO {

    private Long id;

    private Long eventId;

    private Long requesterId;

    private OffsetDateTime creationDate;

    private ParticipationRequestState state;

}
