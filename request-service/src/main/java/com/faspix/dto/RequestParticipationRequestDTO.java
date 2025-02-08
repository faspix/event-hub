package com.faspix.dto;

import com.faspix.enums.ParticipationRequestState;

import java.util.List;

public class RequestParticipationRequestDTO {

    List<Long> requestIds;

    ParticipationRequestState status;

}
