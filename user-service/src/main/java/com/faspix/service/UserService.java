package com.faspix.service;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.entity.User;

public interface UserService {

    User createUser(RequestUserDTO userDTO);

    User editUser(Long userId, RequestUserDTO userDTO);

    Boolean deleteUser(Long userId);

}
