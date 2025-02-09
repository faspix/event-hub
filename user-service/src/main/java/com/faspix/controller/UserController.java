package com.faspix.controller;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.mapper.UserMapper;
import com.faspix.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseUserDTO createUser(
            @RequestBody @Valid RequestUserDTO userDTO
    ) {
        return userMapper.userToResponse(
                userService.createUser(userDTO)
        );
    }

    @PatchMapping
    public ResponseUserDTO editUser(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid RequestUserDTO userDTO
    ) {
        return userMapper.userToResponse(
                userService.editUser(userId, userDTO)
        );
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<HttpStatus> deleteUser(
            @RequestHeader("X-User-Id") Long userId
    ) {
        Boolean result = userService.deleteUser(userId);
        return result ?
                ResponseEntity.ok(HttpStatus.OK)
                : ResponseEntity.internalServerError().build();
    }

    @GetMapping("{userId}")
    public ResponseUserDTO findUserById(
            @PathVariable Long userId
    ) {
        return userMapper.userToResponse(
                userService.findUserById(userId)
        );
    }

}
