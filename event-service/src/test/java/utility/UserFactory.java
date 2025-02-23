package utility;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;

public class UserFactory {


    public static RequestUserDTO makeRequestUserTest() {
        return RequestUserDTO.builder()
                .username("UserName")
                .email("mail@mail.com")
                .build();
    }

    public static ResponseUserDTO makeResponseUserTest() {
        return ResponseUserDTO.builder()
                .username("UserName")
                .email("mail@mail.com")
                .build();
    }


    public static ResponseUserShortDTO makeResponseShortUser() {
        return ResponseUserShortDTO.builder()
                .userId("1")
                .username("name")
                .build();
    }

}
