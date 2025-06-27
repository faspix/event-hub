package com.faspix.notificationservice.service;

import com.faspix.shared.dto.NotificationDTO;

public interface NotificationService {

    void sendConfirmedRequestNotification(NotificationDTO notification);

    void sendRejectedRequestNotification(NotificationDTO notification);

}
