package com.faspix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestUpdatePasswordDTO {

    @NotBlank(message = "Password shouldn't be blank")
    @Size(min = 3, message = "Password should be min 3 signs")
    private String password;

}
