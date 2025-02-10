package utility;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;

public class UserFactory {


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
