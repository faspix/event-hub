package com.faspix.service;

import com.faspix.dto.RequestUserDTO;
import com.faspix.entity.User;
import com.faspix.exception.UserNotFoundException;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Override
    public User createUser(RequestUserDTO userDTO) {
        return userRepository.saveAndFlush(
                userMapper.requestToUser(userDTO)
        );
    }

    @Override
    public User editUser(Long userId, RequestUserDTO userDTO) {
        findUserById(userId);
        User updatedUser = userMapper.requestToUser(userDTO);
        updatedUser.setUserId(userId);
        return userRepository.save(updatedUser);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id " + userId + " not found")
        );
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with email " + email + " not found")
        );

    }

    @Override
    public Boolean deleteUser(Long userId) {
        findUserById(userId);
        userRepository.deleteById(userId);
        return true;
    }
}
