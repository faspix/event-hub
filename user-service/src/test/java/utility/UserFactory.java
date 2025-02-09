package utility;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.entity.User;

public class UserFactory {

    public static User makeUserTest() {
        return User.builder()
                .userId(1L)
                .name("UserName")
                .email("mail@mail.com")
                .build();
    }

    public static RequestUserDTO makeRequestUserTest() {
        return RequestUserDTO.builder()
                .name("UserName")
                .email("mail@mail.com")
                .build();
    }

    public static ResponseUserDTO makeResponseUserTest() {
        return ResponseUserDTO.builder()
                .name("UserName")
                .email("mail@mail.com")
                .build();
    }



}
