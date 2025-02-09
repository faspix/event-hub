package com.faspix.dto;

import com.faspix.enums.ParticipationRequestState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseParticipationRequestDTO {

    private Long id;

    private Long eventId;

    private Long requesterId;

    private OffsetDateTime creationDate;

    private ParticipationRequestState state;

}
