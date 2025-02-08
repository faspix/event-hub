package com.faspix.dto;

import com.faspix.enums.ParticipationRequestState;
import lombok.Data;

import java.util.List;

@Data
public class RequestParticipationRequestDTO {

    List<Long> requestIds;

    ParticipationRequestState status;

}
