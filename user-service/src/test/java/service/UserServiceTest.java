package service;

import com.faspix.dto.RequestUserDTO;
import com.faspix.entity.User;
import com.faspix.exception.UserAlreadyExistException;
import com.faspix.exception.UserNotFoundException;
import com.faspix.mapper.UserMapper;
import com.faspix.repository.UserRepository;
import com.faspix.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static utility.UserFactory.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void createUserTest_Success() {
        User user = makeUserTest();
        RequestUserDTO requestUserDTO = makeRequestUserTest();
        when(userRepository.saveAndFlush(any()))
                .thenReturn(user);

        User userDTO = userService.createUser(requestUserDTO);

        assertThat(userDTO, equalTo(user));
        verify(userRepository, times(1)).saveAndFlush(any());
        verify(userMapper, times(1)).requestToUser(any());
    }

    @Test
    public void createUserTest_AlreadyExists_Exception() {
        RequestUserDTO requestUserDTO = makeRequestUserTest();

        when(userRepository.saveAndFlush(any()))
                .thenThrow(new DataIntegrityViolationException(""));

        UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class,
                () -> userService.createUser(requestUserDTO)
        );

        assertEquals("User with email mail@mail.com already exist", exception.getMessage());

    }

    @Test
    public void editUserTest_Success() {
        User user = makeUserTest();
        user.setEmail("updated@mail.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any()))
                .thenReturn(user);

        RequestUserDTO dtoForUpdate = makeRequestUserTest();
        dtoForUpdate.setEmail("updated@mail.com");

        User updatedUser = userService.editUser(user.getUserId(), dtoForUpdate);

        assertThat(updatedUser.getEmail(), equalTo(dtoForUpdate.getEmail()));
        assertThat(updatedUser.getName(), equalTo(dtoForUpdate.getName()));

        verify(userRepository, times(1)).findById(user.getUserId());
        verify(userRepository, times(1)).saveAndFlush(any());
        verify(userMapper, times(1)).requestToUser(any());
    }

    @Test
    public void editUserTest_NotFound_Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        RequestUserDTO dtoForUpdate = makeRequestUserTest();

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.editUser(1L, dtoForUpdate)
        );

        assertEquals("User with id 1 not found", exception.getMessage());
    }

    @Test
    public void deleteUserTest_Success() {
        User user = makeUserTest();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Boolean result = userService.deleteUser(1L);

        assertThat(result, equalTo(true));

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteUserTest_NotFound_Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(1L)
        );

        assertEquals("User with id 1 not found", exception.getMessage());
    }

    @Test
    public void findUserByIdTest_Success() {
        User user = makeUserTest();
        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        User foundUser = userService.findUserById(user.getUserId());

        assertThat(foundUser, equalTo(user));
        verify(userRepository, times(1)).findById(user.getUserId());
    }

    @Test
    public void findUserByIdTest_NotFound_Exception() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.findUserById(1L)
        );

        assertEquals("User with id 1 not found", exception.getMessage());
    }

    @Test
    public void findUserByEmailTest() {
        User user = makeUserTest();
        when(userRepository.findUserByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        User foundUser = userService.findUserByEmail(user.getEmail());

        assertThat(foundUser, equalTo(user));
        verify(userRepository, times(1)).findUserByEmail(user.getEmail());
    }

    @Test
    public void findUserByEmailNotFoundTest() {
        when(userRepository.findUserByEmail(anyString()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.findUserByEmail("nonExist@mail.com")
        );

        assertEquals("User with email nonExist@mail.com not found", exception.getMessage());
    }



}