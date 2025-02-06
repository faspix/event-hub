package com.faspix.mapper;

import com.faspix.client.UserServiceClient;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.entity.Event;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapperUtil {

    private final UserServiceClient userServiceClient;

    private final UserMapper userMapper;

    @Named("getInitiator")
    ResponseUserShortDTO getInitiator(Long initiatorId) {
        ResponseUserDTO responseUserDTO = userServiceClient.getUserById(initiatorId);
        return userMapper.responseUserDtoToResponseUserShortDto(responseUserDTO);
    }

}
