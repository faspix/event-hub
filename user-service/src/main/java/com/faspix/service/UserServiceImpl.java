package com.faspix.service;

import com.faspix.dto.RequestUserDTO;
import com.faspix.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User createUser(RequestUserDTO userDTO) {
        return null;
    }

    @Override
    public User editUser(Long userId, RequestUserDTO userDTO) {
        return null;
    }

    @Override
    public Boolean deleteUser(Long userId) {
        return null;
    }
}
