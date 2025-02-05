package com.faspix.mapper;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ResponseUserDTO userToResponse(User user);

    User requestToUser(RequestUserDTO userDTO);

}
