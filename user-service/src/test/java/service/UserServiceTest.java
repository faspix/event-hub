package service;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.entity.User;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.UserRepository;
import com.faspix.service.UserService;
import com.faspix.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static utility.dto.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void createUserTest() {
        User user = makeUserTest();
        RequestUserDTO requestUserDTO = makeRequestUserTest();
        when(userRepository.saveAndFlush(any()))
                .thenReturn(user);

        User userDTO = userService.createUser(requestUserDTO);

        assertThat(userDTO, equalTo(user));
    }

    @Test
    public void editUserTest() {
        User user = makeUserTest();
        user.setEmail("updated@mail.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.saveAndFlush(any()))
                .thenReturn(user);
        RequestUserDTO dtoForUpdate = makeRequestUserTest();
        dtoForUpdate.setEmail("updated@mail.com");

        User updatedUser = userService.editUser(user.getUserId(), dtoForUpdate);
        assertThat(updatedUser.getEmail(), equalTo(dtoForUpdate.getEmail()));
        assertThat(updatedUser.getName(), equalTo(dtoForUpdate.getName()));
        System.out.println(updatedUser);
    }

    @Test
    public void deleteUserTest() {
        User user = makeUserTest();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        Boolean result = userService.deleteUser(1L);
        assertThat(true, equalTo(result));

    }


}
