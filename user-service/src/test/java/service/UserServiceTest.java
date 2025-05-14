package service;

import com.faspix.dao.UserDAO;
import com.faspix.dto.RequestUpdatePasswordDTO;
import com.faspix.dto.RequestUserAdminEditDTO;
import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.exception.UserAlreadyExistException;
import com.faspix.exception.UserNotFoundException;
import com.faspix.shared.dto.ResponseUserShortDTO;
import com.faspix.shared.roles.UserRoles;
import com.faspix.service.UpdateUsernameService;
import com.faspix.service.UserServiceImpl;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import utility.UserFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private RealmResource realmResource;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private RoleMappingResource roleMappingResource;

    @Mock
    private UpdateUsernameService updateUsernameService;

    @Mock
    private RolesResource rolesResource;

    @Mock
    private RoleResource roleResource;

    @Mock
    private Cache cache;

    @Mock
    private Response response;


    @Test
    public void editUser_Success() {
        String userId = "1";
        RequestUserDTO requestDTO = UserFactory.makeRequestUser();
        ResponseUserDTO expectedResponse = UserFactory.makeResponseUser();
        UserRepresentation userRepresentation = new UserRepresentation();

        when(realmResource.users())
                .thenReturn(usersResource);
        when(usersResource.get(userId))
                .thenReturn(userResource);
        when(userResource.toRepresentation())
                .thenReturn(userRepresentation);

        ResponseUserDTO result = userService.editUser(userId, requestDTO);

        assertThat(result.getUserId(), equalTo(expectedResponse.getUserId()));
        assertThat(result.getUsername(), equalTo(expectedResponse.getUsername()));
        assertThat(result.getEmail(), equalTo(expectedResponse.getEmail()));
        verify(userResource, times(1)).update(any(UserRepresentation.class));
    }

    @Test
    public void updateUserPassword_Success() {
        String userId = "1";
        RequestUpdatePasswordDTO passwordDTO = RequestUpdatePasswordDTO
                .builder()
                .password("newPassword")
                .build();


        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);

        userService.updateUserPassword(userId, passwordDTO);

        verify(userResource, times(1)).resetPassword(any(CredentialRepresentation.class));
    }

    @Test
    public void findUserById_Success() {
        String userId = "1";
        ResponseUserDTO expectedResponse = UserFactory.makeResponseUser();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.setUsername(expectedResponse.getUsername());
        userRepresentation.setEmail(expectedResponse.getEmail());

        when(realmResource.users())
                .thenReturn(usersResource);
        when(usersResource.get(userId))
                .thenReturn(userResource);
        when(userResource.toRepresentation())
                .thenReturn(userRepresentation);
        when(userResource.roles())
                .thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel())
                .thenReturn(mock(RoleScopeResource.class));
        when(userResource.roles().realmLevel().listAll())
                .thenReturn(Collections.singletonList(new RoleRepresentation()));

        ResponseUserDTO result = userService.findUserById(userId);

        assertThat(result.getUserId(), equalTo(expectedResponse.getUserId()));
        assertThat(result.getUsername(), equalTo(expectedResponse.getUsername()));
        assertThat(result.getEmail(), equalTo(expectedResponse.getEmail()));
        verify(usersResource, times(2)).get(userId);
    }

    @Test
    public void findUserById_NotFound_ThrowsException() {
        String userId = "1";

        when(realmResource.users())
                .thenReturn(usersResource);
        when(usersResource.get(userId))
                .thenReturn(userResource);
        when(userResource.toRepresentation())
                .thenThrow(new NotFoundException());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(userId));
        verify(usersResource, times(1)).get(userId);
    }

    @Test
    public void adminEditUser_Success() {
        String userId = "1";
        RequestUserAdminEditDTO requestDTO = RequestUserAdminEditDTO.builder()
                .username("NewUsername")
                .email("newEmail@mail.com")
                .addRole(UserRoles.USER)
                .build();
        UserRepresentation userRepresentation = new UserRepresentation();

        when(realmResource.users())
                .thenReturn(usersResource);
        when(usersResource.get(userId))
                .thenReturn(userResource);
        when(userResource.toRepresentation())
                .thenReturn(userRepresentation);
        when(realmResource.roles())
                .thenReturn(rolesResource);
        when(rolesResource.get("USER"))
                .thenReturn(mock(RoleResource.class));
        when(rolesResource.get("USER")
                .toRepresentation()).thenReturn(new RoleRepresentation());
        when(userResource.roles())
                .thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel())
                .thenReturn(mock(RoleScopeResource.class));
        when(userResource.roles().realmLevel().listAll())
                .thenReturn(Collections.singletonList(new RoleRepresentation()));

        ResponseUserDTO result = userService.adminEditUser(userId, requestDTO);

        assertThat(result.getEmail(), equalTo(requestDTO.getEmail()));
        assertThat(result.getUsername(), equalTo(requestDTO.getUsername()));
        verify(userResource, times(1)).update(any(UserRepresentation.class));
        verify(usersResource.get(userId).roles().realmLevel(), times(1)).add(anyList());
    }

    @Test
    public void deleteUser_Success() {
        String userId = "1";

        when(realmResource.users())
                .thenReturn(usersResource);
        when(usersResource.get(userId))
                .thenReturn(userResource);

        userService.deleteUser(userId);

        verify(userResource, times(1)).remove();
    }

    @Test
    public void searchUsers_Success() {
        String nickname = "user";
        String email = "mail@mail.com";
        int page = 0;
        int size = 10;
        ResponseUserDTO responseDTO = UserFactory.makeResponseUser();

        when(userDAO.findUsers(nickname, email, page, size)).thenReturn(Collections.singletonList(responseDTO));

        List<ResponseUserDTO> result = userService.searchUsers(nickname, email, page, size);

        assertThat(result.getFirst().getUsername(), equalTo(responseDTO.getUsername()));
        assertThat(result.getFirst().getEmail(), equalTo(responseDTO.getEmail()));
        verify(userDAO, times(1)).findUsers(nickname, email, page, size);
    }

    @Test
    public void findUserByIds_Success() {
        Set<String> userIds = Set.of("1");
        ResponseUserShortDTO responseDTO = UserFactory.makeResponseUserShort();

        when(userDAO.findAll(userIds)).thenReturn(Collections.singletonList(responseDTO));

        List<ResponseUserShortDTO> result = userService.findUserByIds(userIds);

        assertThat(result.getFirst().getUserId(), equalTo(responseDTO.getUserId()));
        assertThat(result.getFirst().getUsername(), equalTo(responseDTO.getUsername()));
        verify(userDAO, times(1)).findAll(userIds);
    }

    @Test
    public void createUser_UserAlreadyExists_ThrowsException() {
        RequestUserDTO requestDTO = RequestUserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(HttpStatus.CONFLICT.value());

        assertThrows(UserAlreadyExistException.class, () -> userService.createUser(requestDTO));
        verify(usersResource, times(1)).create(any(UserRepresentation.class));
    }

    @Test
    public void adminEditUser_RemoveRole_Success() {
        String userId = "1";
        RequestUserAdminEditDTO requestDTO = RequestUserAdminEditDTO.builder()
                .username("NewUsername")
                .email("newEmail@mail.com")
                .removeRole(UserRoles.USER)
                .build();
        UserRepresentation userRepresentation = new UserRepresentation();
        RoleRepresentation roleRepresentation = new RoleRepresentation();

        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);
        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get("USER")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(roleRepresentation);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(mock(RoleScopeResource.class));
        when(userResource.roles().realmLevel().listAll())
                .thenReturn(Collections.singletonList(roleRepresentation));

        ResponseUserDTO result = userService.adminEditUser(userId, requestDTO);

        assertThat(result.getUsername(), equalTo(requestDTO.getUsername()));
        assertThat(result.getEmail(), equalTo(requestDTO.getEmail()));
        verify(userResource, times(1)).update(any(UserRepresentation.class));
        verify(usersResource.get(userId).roles().realmLevel(), times(1)).remove(anyList());
    }


    @Test
    public void setUserPassword_Success() throws Exception {
        String userId = "1";
        String password = "testPassword123";

        when(usersResource.get(userId)).thenReturn(userResource);

        doAnswer(invocation -> {
            CredentialRepresentation credential = invocation.getArgument(0);
            assertThat(credential.getType(), equalTo(CredentialRepresentation.PASSWORD));
            assertThat(credential.getValue(), equalTo(password));
            assertThat(credential.isTemporary(), equalTo(false));
            return null;
        }).when(userResource).resetPassword(any(CredentialRepresentation.class));

        Method setUserPasswordMethod = UserServiceImpl.class
                .getDeclaredMethod("setUserPassword", UsersResource.class, String.class, String.class);
        setUserPasswordMethod.setAccessible(true);

        setUserPasswordMethod.invoke(null, usersResource, userId, password);

        verify(usersResource, times(1)).get(userId);
        verify(userResource, times(1)).resetPassword(any(CredentialRepresentation.class));
    }

}
