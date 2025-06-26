package com.faspix.controller;

import com.faspix.dto.RequestUpdatePasswordDTO;
import com.faspix.dto.RequestUserAdminEditDTO;
import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.service.UserService;
import com.faspix.shared.dto.ResponseUserShortDTO;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseUserDTO createUser(
            @RequestBody @Valid RequestUserDTO userDTO
    ) {
        return userService.createUser(userDTO);
    }

    @PatchMapping
    public ResponseUserDTO editUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid RequestUserDTO userDTO
    ) {
        return userService.editUser(jwt.getSubject(), userDTO);

    }

    @GetMapping
    public List<ResponseUserDTO> searchUsers(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return userService.searchUsers(nickname, email, from, size);
    }

    @PatchMapping("/admin/{userId}")
    public ResponseUserDTO adminEditUser(
            @PathVariable String userId,
            @RequestBody @Valid RequestUserAdminEditDTO userDTO
    ) {
        return userService.adminEditUser(userId, userDTO);

    }

    @PatchMapping("/password")
    public void updateUserPassword(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid RequestUpdatePasswordDTO passwordDTO
    ) {
        userService.updateUserPassword(jwt.getSubject(), passwordDTO);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        userService.deleteUser(jwt.getSubject());
    }

    @GetMapping("{userId}")
    public ResponseUserDTO findUserById(
            @PathVariable String userId
    ) {
        return userService.findUserById(userId);
    }

    @PostMapping("/batch")
    @Hidden
    public List<ResponseUserShortDTO> findUserByIds(
            @RequestBody Set<String> userIds
    ) {
        return userService.findUserByIds(userIds);
    }

    @GetMapping("/email/{userId}")
    public String findUserEmailById(
            @PathVariable String userId
    ) {
        return userService.findUserEmailById(userId);
    }

}
