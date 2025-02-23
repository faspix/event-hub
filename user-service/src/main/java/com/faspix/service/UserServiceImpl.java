package com.faspix.service;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import com.faspix.entity.User;
import com.faspix.exception.UserAlreadyExistException;
import com.faspix.exception.UserNotFoundException;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Keycloak keycloak;

    private final RealmResource realmResource;

    @Override
    public ResponseUserDTO createUser(RequestUserDTO userDTO) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setEnabled(true);

        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            throw new UserAlreadyExistException("User with username " + userDTO.getUsername() + " already exist");
        }

        String userId = usersResource.search(userDTO.getUsername()).get(0).getId();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userDTO.getPassword());
        credential.setTemporary(false);
        usersResource.get(userId).resetPassword(credential);

        return ResponseUserDTO.builder()
                .userId(userId)
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .build();
    }

    @Override
    public ResponseUserDTO editUser(Long userId, RequestUserDTO userDTO) {

    return null;
    }

    @Override
    public ResponseUserDTO findUserById(Long userId) {

        return null;
    }

    @Override
    public ResponseUserDTO findUserByEmail(String email) {
        return null;
    }

    @Override
    public Boolean deleteUser(Long userId) {
        return true;
    }

    @Override
    public List<ResponseUserShortDTO> findUserByIds(Set<Long> userIds) {
        return List.of();
    }

}
