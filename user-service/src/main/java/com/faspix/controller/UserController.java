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
    public ResponseUserDTO createUser(
            @RequestBody @Valid RequestUserDTO userDTO
    ) {
        return userMapper.userToResponse(
                userService.createUser(userDTO)
        );
    }

    @PatchMapping("{userId}")
    public ResponseUserDTO editUser(
            @PathVariable Long userId,
            @RequestBody @Valid RequestUserDTO userDTO
    ) {
        return userMapper.userToResponse(
                userService.editUser(userId, userDTO)
        );
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<HttpStatus> deleteUser(
            @PathVariable Long userId
    ) {
        Boolean result = userService.deleteUser(userId);
        return result ?
                ResponseEntity.ok(HttpStatus.OK)
                : ResponseEntity.internalServerError().build();
    }



}
