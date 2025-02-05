package com.faspix.service;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.entity.User;

public interface UserService {

    User createUser(RequestUserDTO userDTO);

    User editUser(Long userId, RequestUserDTO userDTO);

    User findUserById(Long userId);

    User findUserByEmail(String email);

    Boolean deleteUser(Long userId);

}
