package com.faspix.service;

import com.faspix.dto.*;
import com.faspix.exception.UserAlreadyExistException;
import com.faspix.exception.UserNotFoundException;
import com.faspix.roles.UserRoles;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final RealmResource realmResource;

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
        return ResponseUserDTO.builder()
                .userId(userId)
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .build();
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseUserDTO editUser(String userId, RequestUserDTO userDTO) {
        UserResource userResource = realmResource.users().get(userId);

        UserRepresentation userRepresentation = userResource.toRepresentation();
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
    public ResponseUserDTO findUserById(String userId) {
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(userId);
        UserRepresentation userRepresentation;
        try {
            userRepresentation = userResource.toRepresentation();
        } catch (NotFoundException e) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        return ResponseUserDTO.builder()
                .userId(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .email(userRepresentation.getEmail())
                .roles(getUserRoles(userId, usersResource))
                .build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseUserDTO adminEditUser(String userId, RequestUserAdminEditDTO userDTO) {
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(userId);

        UserRepresentation userRepresentation;
        try {
            userRepresentation = userResource.toRepresentation();
        } catch (NotFoundException e) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }

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
    public void deleteUser(String userId) {
        UserResource userResource = realmResource.users().get(userId);
        userResource.remove();
    }

    // TODO: role and batch update fix
    @PreAuthorize("hasAnyRole('MICROSERVICE')")
    @Override
    public List<ResponseUserShortDTO> findUserByIds(Set<String> userIds) {
        return userIds.stream()
                .map(id -> {
                    UserResource userResource = realmResource.users().get(id);
                    UserRepresentation userRepresentation = userResource.toRepresentation();
                    return ResponseUserShortDTO.builder()
                            .userId(userRepresentation.getId())
                            .username(userRepresentation.getUsername())
                            .build();
                })
                .collect(Collectors.toList());
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

    // TODO: fix service roles
    private static List<String> getUserRoles(String userId, UsersResource usersResource) {
        return usersResource
                .get(userId)
                .roles()
                .realmLevel()
                .listAll()
                .stream()
                .map(RoleRepresentation::toString)
//                .map(UserRoles::valueOf)
//                .map(Enum::toString)
                .toList();
    }

}
