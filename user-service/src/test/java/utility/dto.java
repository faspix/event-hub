package utility;

import com.faspix.dto.RequestUserDTO;
import com.faspix.dto.ResponseUserDTO;
import com.faspix.entity.User;

public class dto {

    public static User makeUserTest() {
        return new User(
                1L,
                "UserName",
                "mail@mail.com"
        );
    }

    public static RequestUserDTO makeRequestUserTest() {
        return new RequestUserDTO(
                "UserName",
                "mail@mail.com"
        );
    }

    public static ResponseUserDTO makeResponseUserTest() {
        return new ResponseUserDTO (
                1L,
                "UserName",
                "mail@mail.com"
        );
    }



}
