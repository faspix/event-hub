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
        return userService.createUser(userDTO);
    }

    @PatchMapping
    public ResponseUserDTO editUser(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid RequestUserDTO userDTO
    ) {
        return userService.editUser(userId, userDTO);

    }

    // TODO: return value
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @RequestHeader("X-User-Id") Long userId
    ) {
        userService.deleteUser(userId);
    }

    @GetMapping("{userId}")
    public ResponseUserDTO findUserById(
            @PathVariable Long userId
    ) {
        return userService.findUserById(userId);
    }

}
