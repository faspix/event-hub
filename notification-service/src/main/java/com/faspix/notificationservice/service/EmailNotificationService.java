package com.faspix.notificationservice.service;

import com.faspix.notificationservice.client.UserServiceClient;
import com.faspix.shared.dto.ConfirmedRequestNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {

    private final UserServiceClient userServiceClient;

    @Override
    public void sendConfirmedRequestNotification(ConfirmedRequestNotificationDTO notification) {
        String userEmail = userServiceClient.getEmailById(notification.getUserId());
    }
}
