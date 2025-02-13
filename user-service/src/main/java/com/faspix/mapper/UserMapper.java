package com.faspix.mapper;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ResponseUserDTO userToResponse(User user);

    ResponseUserShortDTO userToShortResponse(User user);

    User requestToUser(RequestUserDTO userDTO);

}
