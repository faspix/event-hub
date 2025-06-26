package com.faspix.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmedRequestNotificationDTO {

    private final String userId;

    private final String eventName;

    private final Boolean isConfirmed;

}
