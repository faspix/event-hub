package com.faspix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class RequestUserDTO {

    @NotBlank(message = "Name shouldn't be blank")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email shouldn't be blank")
    private String email;


}
