package com.faspix.notificationservice.service;

import com.faspix.notificationservice.client.UserServiceClient;
import com.faspix.notificationservice.dto.UserDTO;
import com.faspix.shared.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService implements NotificationService {

    private final UserServiceClient userServiceClient;

    private final CacheManager cacheManager;

    private final EmailService emailService;

    @Override
    public void sendConfirmedRequestNotification(NotificationDTO notification) {
        String userEmail = getEmailByUserId(notification.getUserId());
        emailService.sendEmail(userEmail, "", "");
    }

    @Override
    public void sendRejectedRequestNotification(NotificationDTO notification) {
        String userEmail = getEmailByUserId(notification.getUserId());
        emailService.sendEmail(userEmail, "", "");
    }

    private String getEmailByUserId(String userId) {
        Cache cache = cacheManager.getCache("UserService::getUserById");
        if (cache == null) {
            log.error("Cache UserService::getUserById is null");
            return userServiceClient.getEmailById(userId);
        }
        UserDTO userDTO = cache.get(userId, UserDTO.class);
        if (userDTO == null) {
            log.error("Cache UserService::getUserById for user with id {} is null", userId);
            String emailById = userServiceClient.getEmailById(userId);
            cache.put(userId, emailById);
            return emailById;
        }
        return userDTO.getEmail();
    }

}
