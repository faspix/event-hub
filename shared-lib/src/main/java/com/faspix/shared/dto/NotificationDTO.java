package com.faspix.shared.dto;

import com.faspix.shared.utility.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO {

    private NotificationType type;

    private String userId;

    private String eventName;

}
