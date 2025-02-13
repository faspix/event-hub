package com.faspix.service;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseCategoryDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.entity.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    ResponseUserDTO createUser(RequestUserDTO userDTO);

    ResponseUserDTO editUser(Long userId, RequestUserDTO userDTO);

    ResponseUserDTO findUserById(Long userId);

    ResponseUserDTO findUserByEmail(String email);

    Boolean deleteUser(Long userId);

    List<ResponseUserShortDTO> findUserByIds(Set<Long> userIds);
}
