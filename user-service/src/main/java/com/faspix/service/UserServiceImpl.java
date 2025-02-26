package com.faspix.service;

import com.faspix.dto.RequestUpdatePasswordDTO;
import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.exception.UserAlreadyExistException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
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

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userDTO.getPassword());
        credential.setTemporary(false);
        usersResource.get(userId).resetPassword(credential);

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
        userRepresentation.setUsername(userDTO.getUsername());
        userRepresentation.setEmail(userDTO.getEmail());

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
        UserResource userResource = realmResource.users().get(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        return ResponseUserDTO.builder()
                .userId(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .email(userRepresentation.getEmail())
                .build();
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void deleteUser(String userId) {
        UserResource userResource = realmResource.users().get(userId);
        userResource.remove();
    }

    // TODO: role and batch update fix
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

}
