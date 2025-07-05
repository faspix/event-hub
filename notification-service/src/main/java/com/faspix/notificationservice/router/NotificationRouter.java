package com.faspix.notificationservice.router;

import com.faspix.notificationservice.service.NotificationService;
import com.faspix.shared.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRouter {

    private final NotificationService notificationService;

    public void receiveNotification(NotificationDTO notification) {
        switch (notification.getType()) {
            case REQUEST_CONFIRMED -> notificationService.sendConfirmedRequestNotification(notification);
            case REQUEST_REJECTED -> notificationService.sendRejectedRequestNotification(notification);
            default -> throw new IllegalArgumentException("Invalid notification type");
        }
    }

}
