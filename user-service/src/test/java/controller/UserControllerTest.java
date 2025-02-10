package controller;

import com.faspix.UserApplication;
import com.faspix.controller.UserController;
import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseParticipationRequestDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.entity.User;
import com.faspix.exception.UserAlreadyExistException;
import com.faspix.exception.UserNotFoundException;
import com.faspix.repository.UserRepository;
import com.faspix.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utility.UserFactory.*;

@SpringBootTest(classes = {UserApplication.class})
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        userRepository.deleteAll();
    }

    @Test
    public void createUserTest_Success() throws Exception {
        RequestUserDTO requestUserDTO = makeRequestUserTest();

        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(requestUserDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseUserDTO userDTO = objectMapper.readValue(body, ResponseUserDTO.class);

        User findUserDTO = userRepository.findUserByEmail(requestUserDTO.getEmail()).get();
        assertThat(findUserDTO.getName(), equalTo(userDTO.getName()));
        assertThat(findUserDTO.getEmail(), equalTo(userDTO.getEmail()));
    }

    @Test
    public void createUserTest_EmailAlreadyExist_Exception() {
        RequestUserDTO requestUserDTO = makeRequestUserTest();
        userController.createUser(requestUserDTO);
        UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class,
                () -> userController.createUser(requestUserDTO)
        );
        assertEquals("User with email mail@mail.com already exist", exception.getMessage());
    }

    @Test
    public void deleteUserTest_Success() {
        User user = makeUserTest();
        user.setUserId(null);
        userRepository.save(user);

        ResponseEntity<HttpStatus> result = userController.deleteUser(user.getUserId());
        assertThat(result, equalTo(ResponseEntity.ok(HttpStatus.OK)));
    }

    @Test
    public void deleteUserTest_UserNotFound_Exception() {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.deleteUser(100L)
        );
        assertEquals("User with id 100 not found", exception.getMessage());
    }


    @Test
    public void editUserTest_Success() {
        User user = makeUserTest();
        user.setUserId(null);
        User savedUser = userRepository.save(user);
        RequestUserDTO dtoForUpdate = makeRequestUserTest();
        dtoForUpdate.setEmail("updated@mail.com");

        ResponseUserDTO updatedUser = userController.editUser(savedUser.getUserId(), dtoForUpdate);

        User findUserDTO = userRepository.findUserByEmail(dtoForUpdate.getEmail()).get();
        assertThat(findUserDTO.getName(), equalTo(updatedUser.getName()));
        assertThat(findUserDTO.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    public void editUserTest_UserNotFound_Exception() {
        RequestUserDTO dtoForUpdate = makeRequestUserTest();
        dtoForUpdate.setEmail("updated@mail.com");

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.editUser(100L, dtoForUpdate)
        );
        assertEquals("User with id 100 not found", exception.getMessage());
    }

    @Test
    public void findUserTest() {
        User user2 = makeUserTest();
        user2.setUserId(null);
        User savedUser = userRepository.save(user2);

        ResponseUserDTO user = userController.findUserById(savedUser.getUserId());
        assertThat(user2.getEmail(), equalTo(user.getEmail()));
        assertThat(user2.getName(), equalTo(user.getName()));
    }

    @Test
    public void findUserNotFoundTest() {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.findUserById(100L));
        assertEquals("User with id 100 not found", exception.getMessage());
    }
}
