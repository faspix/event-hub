package utility;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.shared.dto.ResponseUserShortDTO;

import java.util.Collections;

public class UserFactory {

    public static ResponseUserDTO makeResponseUser() {
        return ResponseUserDTO.builder()
                .userId("1")
                .email("mail@mail.com")
                .roles(Collections.singletonList("USER"))
                .username("username")
                .build();
    }

    public static ResponseUserShortDTO makeResponseUserShort() {
        return ResponseUserShortDTO.builder()
                .userId("1")
                .username("username")
                .build();
    }

    public static RequestUserDTO makeRequestUser() {
        return RequestUserDTO.builder()
                .email("mail@mail.com")
                .username("username")
                .password("123123")
                .build();
    }

}
