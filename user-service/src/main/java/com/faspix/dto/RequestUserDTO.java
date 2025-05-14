package com.faspix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestUserDTO {

    @NotBlank(message = "Name shouldn't be blank")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email shouldn't be blank")
    private String email;

    @NotBlank(message = "Password shouldn't be blank")
    private String password;

}
