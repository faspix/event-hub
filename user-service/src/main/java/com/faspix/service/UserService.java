package com.faspix.service;

import com.faspix.dto.RequestUpdatePasswordDTO;
import com.faspix.dto.RequestUserAdminEditDTO;
import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.shared.dto.ResponseUserShortDTO;

import java.util.List;
import java.util.Set;

public interface UserService {

    ResponseUserDTO createUser(RequestUserDTO userDTO);

    ResponseUserDTO editUser(String userId, RequestUserDTO userDTO);

    ResponseUserDTO findUserById(String userId);

    ResponseUserDTO adminEditUser(String userId, RequestUserAdminEditDTO requestDTO);

    void updateUserPassword(String userId, RequestUpdatePasswordDTO passwordDTO);

    void deleteUser(String userId);

    List<ResponseUserDTO> searchUsers(String nickname, String email, Integer from, Integer size);

    List<ResponseUserShortDTO> findUserByIds(Set<String> userIds);
}
