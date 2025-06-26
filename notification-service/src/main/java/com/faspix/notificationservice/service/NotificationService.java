package com.faspix.notificationservice.service;

import com.faspix.shared.dto.ConfirmedRequestNotificationDTO;

public interface NotificationService {

    void sendConfirmedRequestNotification(ConfirmedRequestNotificationDTO notification);

}
