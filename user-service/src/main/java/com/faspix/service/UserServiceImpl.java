package com.faspix.service;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.entity.User;
import com.faspix.exception.UserAlreadyExistException;
import com.faspix.exception.UserNotFoundException;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseUserDTO createUser(RequestUserDTO userDTO) {
        try {
            User user = userRepository.saveAndFlush(
                    userMapper.requestToUser(userDTO)
            );
            return userMapper.userToResponse(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistException("User with email " + userDTO.getEmail() + " already exist");
        }
    }

    @Override
    @Transactional
    public ResponseUserDTO editUser(Long userId, RequestUserDTO userDTO) {
        findUserById(userId);
        User updatedUser = userMapper.requestToUser(userDTO);
        updatedUser.setUserId(userId);
        try {
            return userMapper.userToResponse(
                    userRepository.saveAndFlush(updatedUser)
            );
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistException("User with email " + userDTO.getEmail() + " already exist");
        }

    }

    @Override
    public ResponseUserDTO findUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id " + userId + " not found")
        );
        return userMapper.userToResponse(user);
    }

    @Override
    public ResponseUserDTO findUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with email " + email + " not found")
        );
        return userMapper.userToResponse(user);
    }

    @Override
    @Transactional
    public Boolean deleteUser(Long userId) {
        findUserById(userId);
        userRepository.deleteById(userId);
        return true;
    }

    @Override
    public List<ResponseUserShortDTO> findUserByIds(Set<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(userMapper::userToShortResponse)
                .toList();
    }

}
