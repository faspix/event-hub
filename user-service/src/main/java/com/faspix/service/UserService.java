package com.faspix.service;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.entity.User;

public interface UserService {

    ResponseUserDTO createUser(RequestUserDTO userDTO);

    ResponseUserDTO editUser(Long userId, RequestUserDTO userDTO);

    ResponseUserDTO findUserById(Long userId);

    ResponseUserDTO findUserByEmail(String email);

    Boolean deleteUser(Long userId);

}
