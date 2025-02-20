package com.faspix.client;

import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {
    @Override
    public ResponseUserDTO getUserById(Long userId) {
        log.error("Error during calling user service for getUserById, category id: {} ", userId);
        throw new ServiceUnavailableException("Error during calling user service");
    }

    @Override
    public Set<ResponseUserShortDTO> getUsersByIds(Set<Long> userIds) {
        log.error("Error during calling user service for getUsersByIds, category id: {} ", userIds);
        throw new ServiceUnavailableException("Error during calling user service");
    }
}
