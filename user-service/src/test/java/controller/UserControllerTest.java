package controller;

import com.faspix.UserApplication;
import com.faspix.controller.UserController;
import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.entity.User;
import com.faspix.repository.UserRepository;
import com.faspix.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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


    @Test
    public void createUserTest() {
        RequestUserDTO requestUserDTO = makeRequestUserTest();

        ResponseUserDTO userDTO = userController.createUser(requestUserDTO);
        User findUserDTO = userRepository.findUserByEmail(requestUserDTO.getEmail()).get();
        assertThat(findUserDTO.getName(), equalTo(userDTO.getName()));
        assertThat(findUserDTO.getEmail(), equalTo(userDTO.getEmail()));
    }

    @Test
    public void deleteUserTest() {
        User user = makeUserTest();
        user.setUserId(null);
        user.setEmail("mail7@mail.com");
        userRepository.save(user);

        ResponseEntity<HttpStatus> result = userController.deleteUser(user.getUserId());
        assertThat(result, equalTo(ResponseEntity.ok(HttpStatus.OK)));
    }

    @Test
    public void editUserTest() {
        User user = makeUserTest();
        user.setUserId(null);
        user.setEmail("mail09@mail.com");
        User savedUser = userRepository.save(user);
        RequestUserDTO dtoForUpdate = makeRequestUserTest();
        dtoForUpdate.setEmail("updated@mail.com");

        ResponseUserDTO updatedUser = userController.editUser(savedUser.getUserId(), dtoForUpdate);

        User findUserDTO = userRepository.findUserByEmail(dtoForUpdate.getEmail()).get();
        assertThat(findUserDTO.getName(), equalTo(updatedUser.getName()));
        assertThat(findUserDTO.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    public void findUserTest() {
        User user2 = makeUserTest();
        user2.setUserId(null);
        user2.setEmail("mail019@mail.com");
        User savedUser = userRepository.save(user2);

        ResponseUserDTO user = userController.findUserById(savedUser.getUserId());
        assertThat(user2.getEmail(), equalTo(user.getEmail()));
        assertThat(user2.getName(), equalTo(user.getName()));
    }

}
