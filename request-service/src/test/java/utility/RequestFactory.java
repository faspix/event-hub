package utility;


import com.faspix.dto.RequestParticipationRequestDTO;
import com.faspix.dto.ResponseParticipationRequestDTO;
import com.faspix.entity.Request;
import com.faspix.enums.ParticipationRequestState;

import java.time.OffsetDateTime;
import java.util.Collections;

public class RequestFactory {

    public static Request makeRequest() {
        return Request.builder()
                .id(1L)
                .requesterId(1L)
                .eventId(1L)
                .state(ParticipationRequestState.PENDING)
                .creationDate(OffsetDateTime.now())
                .build();
    }

    public static RequestParticipationRequestDTO makeRequestRequest() {
        return RequestParticipationRequestDTO.builder()
                .requestIds(Collections.singletonList(1L))
                .status(ParticipationRequestState.CONFIRMED)
                .build();
    }

    public static ResponseParticipationRequestDTO makeResponseRequest() {
        return ResponseParticipationRequestDTO.builder()
                .id(1L)
                .requesterId(1L)
                .eventId(1L)
                .state(ParticipationRequestState.PENDING)
                .creationDate(OffsetDateTime.now())
                .build();
    }



}
