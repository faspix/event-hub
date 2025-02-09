package com.faspix.service;

import com.faspix.dto.RequestUserDTO;
import com.faspix.entity.User;
import com.faspix.exception.UserAlreadyExistException;
import com.faspix.exception.UserNotFoundException;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(RequestUserDTO userDTO) {
        try {
            return userRepository.saveAndFlush(
                    userMapper.requestToUser(userDTO)
            );
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistException("User with email " + userDTO.getEmail() + " already exist");
        }
    }

    @Override
    @Transactional
    public User editUser(Long userId, RequestUserDTO userDTO) {
        findUserById(userId);
        User updatedUser = userMapper.requestToUser(userDTO);
        updatedUser.setUserId(userId);
        try {
            return userRepository.saveAndFlush(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistException("User with email " + userDTO.getEmail() + " already exist");
        }

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
    @Transactional
    public Boolean deleteUser(Long userId) {
        findUserById(userId);
        userRepository.deleteById(userId);
        return true;
    }

}
