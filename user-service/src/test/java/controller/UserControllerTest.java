package controller;

import com.faspix.UserApplication;
import com.faspix.controller.UserController;
import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.entity.User;
import com.faspix.repository.UserRepository;
import com.faspix.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    public void createUserTest_EmailAlreadyExist_Exception() throws Exception {
        RequestUserDTO requestUserDTO = makeRequestUserTest();

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(requestUserDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(requestUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict());

    }

    @Test
    public void deleteUserTest_Success() throws Exception {
        User user = makeUserTest();
        user.setUserId(null);
        user = userRepository.save(user);

        mockMvc.perform(delete("/users")
                        .header("X-User-Id", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNoContent());
    }

    @Test
    public void deleteUserTest_UserNotFound_Exception() throws Exception {
        mockMvc.perform(delete("/users")
                .header("X-User-Id", 100)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }


    @Test
    public void editUserTest_Success() throws Exception {
        User user = makeUserTest();
        user.setUserId(null);
        User savedUser = userRepository.save(user);
        RequestUserDTO dtoForUpdate = makeRequestUserTest();
        dtoForUpdate.setEmail("updated@mail.com");

        MvcResult mvcResult = mockMvc.perform(patch("/users")
                        .content(objectMapper.writeValueAsString(dtoForUpdate))
                        .header("X-User-Id", savedUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseUserDTO updatedUser = objectMapper.readValue(body, ResponseUserDTO.class);

        User findUserDTO = userRepository.findUserByEmail(dtoForUpdate.getEmail()).get();
        assertThat(findUserDTO.getName(), equalTo(updatedUser.getName()));
        assertThat(findUserDTO.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    public void editUserTest_UserNotFound_Exception() throws Exception {
        RequestUserDTO dtoForUpdate = makeRequestUserTest();
        dtoForUpdate.setEmail("updated@mail.com");

        mockMvc.perform(patch("/users")
                        .content(objectMapper.writeValueAsString(dtoForUpdate))
                        .header("X-User-Id", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound());
    }

    @Test
    public void findUserTest() throws Exception {
        User user2 = makeUserTest();
        user2.setUserId(null);
        User savedUser = userRepository.save(user2);

        MvcResult mvcResult = mockMvc.perform(get("/users/{userId}", savedUser.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        ResponseUserDTO user = objectMapper.readValue(body, ResponseUserDTO.class);

        assertThat(user2.getEmail(), equalTo(user.getEmail()));
        assertThat(user2.getName(), equalTo(user.getName()));
    }

    @Test
    public void findUserNotFoundTest() throws Exception {
        mockMvc.perform(get("/users/100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }
}
