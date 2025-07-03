package com.faspix.shared.dto;

import com.faspix.shared.utility.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO {

    private final NotificationType type;

    private final String userId;

    private final String eventName;

}
