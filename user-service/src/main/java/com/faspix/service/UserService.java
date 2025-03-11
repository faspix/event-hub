package com.faspix.service;

import com.faspix.dto.*;

import java.util.List;
import java.util.Set;

public interface UserService {

    ResponseUserDTO createUser(RequestUserDTO userDTO);

    ResponseUserDTO editUser(String userId, RequestUserDTO userDTO);

    ResponseUserDTO findUserById(String userId);

    ResponseUserDTO adminEditUser(String userId, RequestUserAdminEditDTO requestDTO);

    void updateUserPassword(String userId, RequestUpdatePasswordDTO passwordDTO);

    void deleteUser(String userId);

    List<ResponseUserDTO> searchUsers(int page, int size);

    List<ResponseUserShortDTO> findUserByIds(Set<String> userIds);
}
