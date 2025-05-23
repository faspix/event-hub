package com.faspix.service;

import com.faspix.dto.RequestUpdatePasswordDTO;
import com.faspix.dto.RequestUserAdminEditDTO;
import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.exception.UserAlreadyExistException;
import com.faspix.exception.UserNotFoundException;
import com.faspix.dao.UserDAO;
import com.faspix.shared.dto.ResponseUserShortDTO;
import com.faspix.shared.dto.UpdateUsernameDTO;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserService implements UserService {

    private final RealmResource realmResource;

    private final CacheManager cacheManager;

    private final UserDAO userDAO;

    private final UpdateUsernameService updateUsernameService;

    @Override
    public ResponseUserDTO createUser(RequestUserDTO userDTO) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setEnabled(true);

        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(user);
        if (response.getStatus() == HttpStatus.CONFLICT.value()) {
            throw new UserAlreadyExistException("User with username this username or email already exist");
        }

        String userId = CreatedResponseUtil.getCreatedId(response);

        setUserPassword(usersResource, userId, userDTO.getPassword());

        RoleRepresentation roleRepresentation = realmResource.roles().get("USER").toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));

        ResponseUserDTO responseDTO = ResponseUserDTO.builder()
                .userId(userId)
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .build();

        Cache cache = cacheManager.getCache("UserService::getUserById");
        if (cache != null) {
            cache.put(userId, responseDTO);
        } else {
            log.error("Cache UserService::getUserById is null");
        }

        return responseDTO;
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @CachePut(value = "UserService::getUserById", key = "#userId")
    public ResponseUserDTO editUser(String userId, RequestUserDTO userDTO) {
        UserResource userResource = realmResource.users().get(userId);

        UserRepresentation userRepresentation = userResource.toRepresentation();
        updateUsernameChecker(userDTO, userRepresentation);
        updateUser(userDTO, userRepresentation);

        userResource.update(userRepresentation);

    return ResponseUserDTO.builder()
            .userId(userId)
            .username(userDTO.getUsername())
            .email(userDTO.getEmail())
            .build();
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void updateUserPassword(String userId, RequestUpdatePasswordDTO passwordDTO) {
        UserResource userResource = realmResource.users().get(userId);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(passwordDTO.getPassword());
        credential.setTemporary(false);

        userResource.resetPassword(credential);
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MICROSERVICE')")
    @Cacheable(value = "UserService::getUserById", key = "#userId")
    public ResponseUserDTO findUserById(String userId) {
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(userId);
        UserRepresentation userRepresentation = getUserRepresentation(userId, userResource);
        return ResponseUserDTO.builder()
                .userId(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .email(userRepresentation.getEmail())
                .roles(getUserRoles(userId, usersResource))
                .build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN')")
    @CachePut(value = "UserService::getUserById", key = "#userId")
    public ResponseUserDTO adminEditUser(String userId, RequestUserAdminEditDTO userDTO) {
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(userId);

        UserRepresentation userRepresentation = getUserRepresentation(userId, userResource);
        updateUsernameChecker(userDTO, userRepresentation);
        updateUser(userDTO, userRepresentation);

        userResource.update(userRepresentation);

        if (userDTO.getAddRole() != null) {
            RoleRepresentation roleRepresentation = realmResource.roles()
                    .get(userDTO.getAddRole().name())
                    .toRepresentation();
            usersResource.get(userId).roles().realmLevel()
                    .add(Collections.singletonList(roleRepresentation));
        }
        if (userDTO.getRemoveRole() != null) {
            RoleRepresentation roleRepresentation = realmResource.roles()
                    .get(userDTO.getRemoveRole().name())
                    .toRepresentation();
            usersResource.get(userId).roles().realmLevel()
                    .remove(Collections.singletonList(roleRepresentation));
        }

        return ResponseUserDTO.builder()
                .userId(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .email(userRepresentation.getEmail())
                .roles(getUserRoles(userId, usersResource))
                .build();
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @CacheEvict(value = "UserService::getUserById", key = "#userId")
    public void deleteUser(String userId) {
        UserResource userResource = realmResource.users().get(userId);
        userResource.remove();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<ResponseUserDTO> searchUsers(String nickname, String email, Integer from, Integer size) {
        return userDAO.findUsers(nickname, email, from, size);
    }

    @PreAuthorize("hasAnyRole('MICROSERVICE')")
    @Override
    public List<ResponseUserShortDTO> findUserByIds(Set<String> userIds) {
        return userDAO.findAll(userIds);
    }

    private static void updateUser(RequestUserAdminEditDTO userDTO, UserRepresentation userRepresentation) {
        if (userDTO.getUsername() != null)
            userRepresentation.setUsername(userDTO.getUsername());
        if (userDTO.getEmail() != null)
            userRepresentation.setEmail(userDTO.getEmail());
    }

    private static void updateUser(RequestUserDTO userDTO, UserRepresentation userRepresentation) {
        if (userDTO.getUsername() != null)
            userRepresentation.setUsername(userDTO.getUsername());
        if (userDTO.getEmail() != null)
            userRepresentation.setEmail(userDTO.getEmail());
    }

    private static void setUserPassword(UsersResource usersResource, String userId, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        usersResource.get(userId).resetPassword(credential);
    }

    private static UserRepresentation getUserRepresentation(String userId, UserResource userResource) {
        UserRepresentation userRepresentation;
        try {
            userRepresentation = userResource.toRepresentation();
        } catch (NotFoundException e) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        return userRepresentation;
    }

    private static List<String> getUserRoles(String userId, UsersResource usersResource) {
        return usersResource
                .get(userId)
                .roles()
                .realmLevel()
                .listAll()
                .stream()
                .map(RoleRepresentation::toString)
                .toList();
    }

    private void updateUsernameChecker(RequestUserAdminEditDTO userDTO, UserRepresentation userRepresentation) {
        if (userDTO.getUsername() != null && !userDTO.getUsername().equals(userRepresentation.getUsername())) {
            updateUsernameService.sendUpdatedUsername(
                    new UpdateUsernameDTO(userRepresentation.getId(), userDTO.getUsername())
            );
        }
    }

    private void updateUsernameChecker(RequestUserDTO userDTO, UserRepresentation userRepresentation) {
        if (userDTO.getUsername() != null && !userDTO.getUsername().equals(userRepresentation.getUsername())) {
            updateUsernameService.sendUpdatedUsername(
                    new UpdateUsernameDTO(userRepresentation.getId(), userDTO.getUsername())
            );
        }
    }

}
