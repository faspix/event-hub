package utility;


import com.faspix.shared.dto.ResponseUserShortDTO;

public class UserFactory {


    public static ResponseUserShortDTO makeResponseShortUser() {
        return ResponseUserShortDTO.builder()
                .userId("1")
                .username("name")
                .build();
    }

}
