package com.faspix.notificationservice.client;

import com.faspix.shared.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public String getEmailById(String id) {
        log.error("Error during calling user service for getEmailById, user id: {} ", id);
        throw new ServiceUnavailableException("Error during calling user service");
    }
}
